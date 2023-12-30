package pl.edu.pw.pap.comment;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

import java.sql.Timestamp;

//Maybe it's possible to have one interface for reviews and comments that includes the text, likes and dislikes
@Entity
@Getter
@Setter
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    private String text;
    private Boolean edited;
    @CreationTimestamp
    private Timestamp created;
    @JsonIgnore
    @ManyToOne
    private Review review;
    @JsonIgnore
    @ManyToOne
    private User user;


    public Comment(String text, Review review, User user) {
        this.text = text;
        this.edited = false;
        review.addComment(this);
        user.addComment(this);
    }

    protected Comment() {
    }

    @PreRemove
    public void preRemove(){
        if (this.user != null) {
            this.user.removeComment(this);
        }
        if (this.review != null) {
            this.review.removeComment(this);
        }
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", text='" + edited + '\'' +
                ", created=" + created.toString() +
                ", review=" + review.getId() +
                ", user=" + user.getId() +
                '}';
    }
}
