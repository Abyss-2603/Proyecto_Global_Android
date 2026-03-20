package es.iesagora.actividad_de_seguimiento.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Cast implements Serializable {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("character")
    private String character;

    @SerializedName("profile_path")
    private String profilePath;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getCharacter() { return character; }
    public String getProfilePath() { return profilePath; }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProfilePath(String profilePath) {
        this.profilePath = profilePath;
    }
}