package pl.edu.pw.pap.comment;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

//Maybe it's possible to have one interface for reviews and comments that includes the text, likes and dislikes
@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private String text;
    private int likes;
    private int dislikes;

    public Comment(String text) {
        this.text = text;
        this.likes = 0;
        this.dislikes = 0;
    }

    protected Comment() {
    }
}
