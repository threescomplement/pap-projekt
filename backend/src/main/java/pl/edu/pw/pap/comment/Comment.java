package pl.edu.pw.pap.comment;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

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


    @JsonIgnore // We don't want this to be visible to a regular user when getting a comment
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CommentReport> reports = new HashSet<>();


    public Comment(String text, Review review, User user) {
        this.text = text;
        this.edited = false;
        review.addComment(this);
        user.addComment(this);
    }

    protected Comment() {
    }

    // the same operations as in the Comment - Report relation
    public void addReport(CommentReport report){
        reports.add(report);
        report.setReported(this);
    }

    public void removeReport(CommentReport report){
        reports.remove(report);
        report.setReported(null);
    }

    @PreRemove
    public void preRemove() {
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
                ", edited='" + edited + '\'' +
                ", created=" + created.toString() +
                ", review=" + review.getId() +
                ", user=" + user.getId() +
                '}';
    }
}
