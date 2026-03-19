package es.iesagora.actividad_de_seguimiento.model;

public class Comentario {
    private String authorUid;
    private String authorName;
    private String text;
    private String fotoUrl;
    private Object createdAt;

    public Comentario() {
    }

    public Comentario(String authorUid, String authorName, String text, String fotoUrl, Object createdAt) {
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.text = text;
        this.fotoUrl = fotoUrl;
        this.createdAt = createdAt;
    }

    public String getAuthorUid() {
        return authorUid;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getText() {
        return text;
    }

    public String getFotoUrl() {
        return fotoUrl;
    }

    public Object getCreatedAt() {
        return createdAt;
    }
}