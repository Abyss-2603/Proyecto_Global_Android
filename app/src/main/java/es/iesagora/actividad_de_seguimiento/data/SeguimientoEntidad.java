package es.iesagora.actividad_de_seguimiento.data;

import java.io.Serializable;

public class SeguimientoEntidad implements Serializable {
    private int idApi;
    private String titulo;
    private String tipo;
    private String fecha;
    private float puntuacion;
    private String rutaImagen;
    private String descripcion;
    private String generos;

    public SeguimientoEntidad() {}

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

    public int getIdApi() { return idApi; }
    public String getTitulo() { return titulo; }
    public String getTipo() { return tipo; }
    public String getFecha() { return fecha; }
    public float getPuntuacion() { return puntuacion; }
    public String getRutaImagen() { return rutaImagen; }
    public String getDescripcion() { return descripcion; }
    public String getGeneros() { return generos; }

    public void setIdApi(int idApi) { this.idApi = idApi; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public void setFecha(String fecha) { this.fecha = fecha; }
    public void setPuntuacion(float puntuacion) { this.puntuacion = puntuacion; }
    public void setRutaImagen(String rutaImagen) { this.rutaImagen = rutaImagen; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setGeneros(String generos) { this.generos = generos; }
}