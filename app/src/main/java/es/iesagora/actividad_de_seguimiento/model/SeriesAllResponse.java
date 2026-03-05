package es.iesagora.actividad_de_seguimiento.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SeriesAllResponse {
    @SerializedName("results")
    private List<Series> results;
    public List<Series> getResults() { return results; }
}
