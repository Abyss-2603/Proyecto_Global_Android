package es.iesagora.actividad_de_seguimiento.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ActorDetails implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("biography")
    private String biography;

    @SerializedName("profile_path")
    private String profilePath;

    @SerializedName("birthday")
    private String birthday;

    @SerializedName("place_of_birth")
    private String placeOfBirth;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getBiography() { return biography; }
    public String getProfilePath() { return profilePath; }
    public String getBirthday() { return birthday; }
    public String getPlaceOfBirth() { return placeOfBirth; }
}