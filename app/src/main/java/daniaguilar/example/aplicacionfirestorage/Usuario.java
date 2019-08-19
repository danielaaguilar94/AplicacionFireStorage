package daniaguilar.example.aplicacionfirestorage;

import java.util.List;

public class Usuario {

    private String id;
    private String nombreDeUsuario;
    private String email;
    private String password;
    private List<Archivo> archivos;


    public Usuario()
    {

    }


    public Usuario(String nombreDeUsuario, String email, String password){
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.password = password;
    }


    public Usuario(String id, String nombreDeUsuario, String email, String password, List<Archivo> archivos) {
        this.id = id;
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.password = password;
        this.archivos = archivos;
    }

    public Usuario(String id, String nombreDeUsuario, String email, String password) {
        this.id = id;
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.password = password;
    }

    public Usuario(String nombreDeUsuario, String email, String password, List<Archivo> archivos){
        this.nombreDeUsuario = nombreDeUsuario;
        this.email = email;
        this.password = password;
        this.archivos = archivos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreDeUsuario() {
        return nombreDeUsuario;
    }

    public void setNombreDeUsuario(String nombreDeUsuario) {
        this.nombreDeUsuario = nombreDeUsuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Archivo> getArchivos() {
        return archivos;
    }

    public void setArchivos(List<Archivo> archivos) {
        this.archivos = archivos;
    }

}

