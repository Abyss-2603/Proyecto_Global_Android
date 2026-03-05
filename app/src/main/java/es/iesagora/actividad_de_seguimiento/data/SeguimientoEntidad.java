package es.iesagora.actividad_de_seguimiento.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.io.Serializable;

@Entity(tableName = "tabla_seguimiento")
public class SeguimientoEntidad implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private int idApi;
    private String titulo;
    private String tipo;
    private String fecha;
    private float puntuacion;
    private String rutaImagen;

    private String descripcion;
    private String generos;

    public SeguimientoEntidad(int idApi, String titulo, String tipo, String fecha, float puntuacion, String rutaImagen, String descripcion, String generos) {
        this.idApi = idApi;
        this.titulo = titulo;
        this.tipo = tipo;
        this.fecha = fecha;
        this.puntuacion = puntuacion;
        this.rutaImagen = rutaImagen;
        this.descripcion = descripcion;
        this.generos = generos;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdApi() { return idApi; }
    public String getTitulo() { return titulo; }
    public String getTipo() { return tipo; }
    public String getFecha() { return fecha; }
    public float getPuntuacion() { return puntuacion; }
    public String getRutaImagen() { return rutaImagen; }
    public String getDescripcion() { return descripcion; }
    public String getGeneros() { return generos; }
}