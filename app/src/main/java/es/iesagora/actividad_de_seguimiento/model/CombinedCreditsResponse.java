package es.iesagora.actividad_de_seguimiento.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class CombinedCreditsResponse {
    @SerializedName("cast")
    private List<CombinedCredit> cast;

    public List<CombinedCredit> getCast() {
        return cast;
    }


    public static class CombinedCredit implements Serializable {
        @SerializedName("id")
        private int id;

        @SerializedName("title") // Usado en películas
        private String title;

        @SerializedName("name") // Usado en series
        private String name;

        @SerializedName("poster_path")
        private String posterPath;

        @SerializedName("media_type") // tipo: "movie" o "tv"
        private String mediaType;

        @SerializedName("vote_average")
        private double voteAverage;

        @SerializedName("release_date") // Películas
        private String releaseDate;

        @SerializedName("first_air_date") // Series
        private String firstAirDate;


        public int getId() { return id; }
        public String getPosterPath() { return posterPath; }
        public String getMediaType() { return mediaType; }
        public double getVoteAverage() { return voteAverage; }


        //Método para obtener el nombre independientemente del tipo
        public String getDisplayTitle() {
            return (title != null && !title.isEmpty()) ? title : name;
        }

        //Método para obtener la fecha independientemente del tipo
        public String getDisplayDate() {
            return (releaseDate != null && !releaseDate.isEmpty()) ? releaseDate : firstAirDate;
        }
    }
}