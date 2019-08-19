package daniaguilar.example.aplicacionfirestorage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {


    private ImageView imagenIniciarSesion;
    private EditText etIngresarCorreo;
    private EditText etIngresarContraseña;
    private Button btnIniciarSesion;
    private TextView tvRegistrarCuenta;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    AlertDialog.Builder dialogo;
    public static final Pattern EMAIL_ADDRESS = Pattern.compile("^[\\w\\\\\\+]+(\\.[\\w\\\\]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z](2,4)$");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        imagenIniciarSesion = findViewById(R.id.imageViewLogin);
        etIngresarCorreo = findViewById(R.id.editTextCorreo);
        etIngresarContraseña = findViewById(R.id.editTextContraseña);
        btnIniciarSesion = findViewById(R.id.botonIniciarSesion);
        tvRegistrarCuenta= findViewById(R.id.textRegistrarCuenta);
        tvRegistrarCuenta.setPaintFlags(tvRegistrarCuenta.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        tvRegistrarCuenta.setText("Si eres nuevo, Registrate aquí");


        mAuth = FirebaseAuth.getInstance();

        btnIniciarSesion.setOnClickListener(this);
        tvRegistrarCuenta.setOnClickListener(this);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser!=null)
                {
                    Toast.makeText(getApplicationContext(), "Bienvenido: "+firebaseUser.getEmail(), Toast.LENGTH_SHORT).show();
                    irPantallaPrincipal();
                }
            }
        };
    }

    private void irPantallaPrincipal()
    {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.addAuthStateListener(authStateListener);
    }
    public void onStop() {
        super.onStop();
        // Check if user is signed in (non-null) and update UI accordingly.
        mAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.botonIniciarSesion:
                dialogo = new AlertDialog.Builder(this);
                final String email = etIngresarCorreo.getText().toString().trim();
                final String password=etIngresarContraseña.getText().toString().trim();
                if (email.equals("")||password.equals("")){
                    validacionCampos();
                }

                else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                irPantallaPrincipal();
                            }

                            else {
                                dialogo.setTitle("Usuario inexistente/válido");
                                dialogo.setMessage("No se pudo loguear usuario");
                                dialogo.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.cancel();

                                    }
                                });
                                AlertDialog alertDialog = dialogo.create();
                                alertDialog.show();

                            }
                        }
                    });
                }
                break;

            case R.id.textRegistrarCuenta:
                irARegistroCuentaEmail();
                break;


        }

    }

    private void irARegistroCuentaEmail() {
        Intent intento = new Intent(LoginActivity.this, RegistroActivity.class);
        startActivity(intento);
    }

    private void validacionCampos() {
        String email = etIngresarCorreo.getText().toString().trim();
        String password=etIngresarContraseña.getText().toString().trim();

        if (email.equals(""))
        {
            etIngresarCorreo.setError("Campo Requerido");
            dialogo.setTitle("Campo requerido").setMessage("Ingrese email").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();

                }
            });
            AlertDialog alertDialog = dialogo.create();
            alertDialog.show();

        }
        else if (password.equals(""))
        {
            etIngresarContraseña.setError("Campo requerido");
            dialogo.setTitle("Campo requerido").setMessage("Ingrese contraseña").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = dialogo.create();
            alertDialog.show();

        }

        if (!validarEmail(email)){
            etIngresarCorreo.setError("Formato inválido");
        }
        else{
            Toast.makeText(this, "Formato de Email válido", Toast.LENGTH_SHORT).show();

        }

    }


    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();

    }


}
