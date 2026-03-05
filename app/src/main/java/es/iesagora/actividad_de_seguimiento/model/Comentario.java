package es.iesagora.actividad_de_seguimiento.model;

public class Comentario {
    private String authorUid;
    private String authorName;
    private String text;
    private Object createdAt;

    public Comentario() {}

    public Comentario(String authorUid, String authorName, String text, Object createdAt) {
        this.authorUid = authorUid;
        this.authorName = authorName;
        this.text = text;
        this.createdAt = createdAt;
    }

    public String getAuthorUid() { return authorUid; }
    public String getAuthorName() { return authorName; }
    public String getText() { return text; }
    public Object getCreatedAt() { return createdAt; }
}