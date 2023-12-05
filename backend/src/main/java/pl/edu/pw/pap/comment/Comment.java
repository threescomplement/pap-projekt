package pl.edu.pw.pap.comment;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

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
    @JsonIgnore
    @ManyToOne
    private Review review;
    @JsonIgnore
    @ManyToOne
    private User user;

    public Comment(String text, Review review, User user) {
        this.text = text;
        this.review = review;
        this.user = user;
        this.likes = 0;
        this.dislikes = 0;
    }

    protected Comment() {
    }

    @PreRemove
    public void removeFromUser(){
        this.user.removeComment(this);
        this.review.removeComment(this);
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                ", review=" + review.getId() +
                ", user=" + user.getId() +
                '}';
    }
}
