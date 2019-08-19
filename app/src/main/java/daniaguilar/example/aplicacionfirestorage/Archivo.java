package daniaguilar.example.aplicacionfirestorage;

public class Archivo {
    private String idUpload;
    private String url;

    public Archivo() {
    }

    public Archivo(String url) {
        this.url = url;
    }

    public Archivo(String idUpload, String url) {
        this.idUpload = idUpload;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getIdUpload() {
        return idUpload;
    }
    public void setIdUpload(String idUpload) {
        this.idUpload = idUpload;
    }
}
