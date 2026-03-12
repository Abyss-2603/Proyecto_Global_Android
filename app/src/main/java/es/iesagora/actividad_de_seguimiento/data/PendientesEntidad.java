package es.iesagora.actividad_de_seguimiento.data;

import com.google.firebase.firestore.ServerTimestamp;
import java.io.Serializable;
import java.util.Date;

public class PendientesEntidad implements Serializable {
    private String userId;
    private int idAPI;
    private String titulo;
    private String descripcion;
    private String posterPath;
    private String backdropPath;
    private String tipo;
    private String info;
    private String generos;

    @ServerTimestamp
    private Date createdAt;

    public PendientesEntidad() {}

    public PendientesEntidad(int idAPI, String titulo, String descripcion, String posterPath, String backdropPath, String tipo, String info, String generos) {
        this.idAPI = idAPI;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.tipo = tipo;
        this.info = info;
        this.generos = generos;
    }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public int getIdAPI() { return idAPI; }
    public void setIdAPI(int idAPI) { this.idAPI = idAPI; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getPosterPath() { return posterPath; }
    public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    public String getBackdropPath() { return backdropPath; }
    public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }
    public String getGeneros() { return generos; }
    public void setGeneros(String generos) { this.generos = generos; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}