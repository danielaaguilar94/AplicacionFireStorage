package daniaguilar.example.aplicacionfirestorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //private Spinner mSpinnerTipoArchivo; //spinner para elegir el tipo de archivo a subir
    private ImageView iv_imagen_firestorage;
    private ImageView iv_imagen_seleccionada; //Solo se mostrará cuando el tipo de archivo sea una imagen
    //private TextView tv_elegir_archivo;
    private TextView tv_mostrar_nombre_archivo; //mostrará el url/nombre archivo de acuerdo a lo seleccionado en elegir archivo
    private Button btn_abrir_archivo; //boton que se encarga de mostrar al usuario desde el almacenamiento del móvil a elegir un archivo
    private Button btn_subir_archivo; //boton para subir a FirebaseStorage y a FirebaseDatabase los datos del archivo y el archivo como tal
    private ProgressDialog mProgressDialog; //Encargado de mostrar el progreso de la subida de archivo al usuario
    private StorageReference mStorageReference; //usando para subir archivos al storage de Firebase
    private DatabaseReference mDatabaseReference; //usado para almacenar los datos como la url/nombre de archivo
    private Uri mImageUri; //url de la imagen o el nombre en el que está guardado en el dispositivo
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int INT_REQUEST_CODE = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStorageReference = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseReference = FirebaseDatabase.getInstance().getReference("uploads");

        iv_imagen_firestorage = findViewById(R.id.image_view_fbstorageIcon);
        iv_imagen_seleccionada = findViewById(R.id.image_view_imagenSeleccionada);
        //tv_elegir_archivo = findViewById(R.id.text_view_eligeTipoArchivo);
        tv_mostrar_nombre_archivo = findViewById(R.id.text_view_archivoNombre);
        btn_abrir_archivo = findViewById(R.id.button_open_file);
        btn_subir_archivo = findViewById(R.id.button_upload);
        //mSpinnerTipoArchivo = findViewById(R.id.spinner_tipo_archivo);

        btn_abrir_archivo.setOnClickListener(this);
        btn_subir_archivo.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.button_open_file:

                // Checamos si el permiso de READ_EXTERNAL_STORAGE fue concedido por el usuario
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                {
                    selecccionarArchivo();
                }
                else
                {
                    //El usuario debe conceder el permiso para lo requerido
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE}, INT_REQUEST_CODE);
                }
                break;
            case R.id.button_upload:
                if (mImageUri != null){ //el usuario a seleccionado el archivo
                    subirArchivo(mImageUri);
                }
                else {
                    Toast.makeText(MainActivity.this, "Selecciona un archivo", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    private void subirArchivo(Uri imageUri) {
        mProgressDialog = new ProgressDialog(MainActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setTitle("Subiendo archivo...");
        mProgressDialog.setProgress(0);
        mProgressDialog.show();
        final StorageReference reference = mStorageReference.child(System.currentTimeMillis()
                + "." + getFileExtension(imageUri));
        reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String url=   uri.toString();
                        String upload = mDatabaseReference.push().getKey();
                        mDatabaseReference.child(upload).setValue(url);

                    }
                });

                            Toast.makeText(MainActivity.this, "Archivo subido a FirebaseStorage exitosamente!!", Toast.LENGTH_SHORT).show();
                            mProgressDialog.hide();
                        }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(MainActivity.this, "Archivo no subido Exitosamente: "+e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                int progreso = (int) (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                mProgressDialog.setProgress(progreso);
            }
        });
    }


    //metodo override que obtiene el resultado de la solicitud del permiso
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checamos si el codico de la solicitud es 9 y el permiso ubicado en la posicion 0 del Arreglo de String es igual al concedido
        if (requestCode==INT_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            //se ejecuta el método seleccionarArchivo
            selecccionarArchivo();
        }
        else
        {
            Toast.makeText(this, "Por favor, proporciona el permiso", Toast.LENGTH_SHORT).show();
        }
    }

    private void selecccionarArchivo() {
        //ofrecemos al usuario a seleccionar un archivo usando file manager
        //Usaremos el Intent

        Intent intent = new Intent();
        intent.setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleccciona la imagen"), PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri filePath) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(filePath));
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //checar si el usuario ha seleccionado un archivo o no
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData(); //proporciona la uri de la imagen seleccionada
            String fileName;
            if (mImageUri.getScheme().equals("file")) {
                fileName = mImageUri.getLastPathSegment();
            } else {
                Cursor cursor = null;
                try {
                    cursor = getContentResolver().query(mImageUri, new String[]{
                            MediaStore.Images.ImageColumns.DISPLAY_NAME
                    }, null, null, null);

                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DISPLAY_NAME));
                        tv_mostrar_nombre_archivo.setText("Un archivo ha sido seleccionado: "+fileName);
                        Log.d("Archivo", "El no,bre del archivo es " + fileName);
                    }
                } finally {

                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
               // tv_mostrar_nombre_archivo.setText("Un archivo ha sido seleccionado: "+fileName+"."+getFileExtension(mImageUri));
                iv_imagen_seleccionada.setImageURI(mImageUri);
        }
        else {
            Toast.makeText(this, "Por favor, selecciona una imagen", Toast.LENGTH_SHORT).show();
        }


    }


}
