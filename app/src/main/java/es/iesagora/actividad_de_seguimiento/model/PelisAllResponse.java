package es.iesagora.actividad_de_seguimiento.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PelisAllResponse {
    @SerializedName("results")
    private List<Peliculas> results;
    public List<Peliculas> getResults() { return results; }
}
