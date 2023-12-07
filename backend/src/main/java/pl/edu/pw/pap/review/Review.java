package pl.edu.pw.pap.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.comment.Comment;

import java.sql.Timestamp;
import java.util.List;


@Entity
@Getter
@Setter
public class Review {

    @EmbeddedId
    ReviewKey id = new ReviewKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course;

    private String opinion;
    private int overallRating; // TODO: Decide which parameters should be included in the review
    @CreationTimestamp
    private Timestamp created;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Review(User user, Course course, String opinion, int overallRating) {
        this.user = user;
        this.course = course;
        this.opinion = opinion;
        this.overallRating = overallRating;
    }

    @PreRemove
    public void clearCommentsAndUser(){
//        this.comments.clear();
        this.user.removeReview(this);
        this.course.removeReview(this);
    }
    public void removeComment(Comment comment){
        this.comments.remove(comment);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", course=" + course.getName() +
                ", opinion='" + opinion + '\'' +
                ", overallRating=" + overallRating +
                ", created=" + created.toString() +
                '}';
    }

    protected Review() {
    }
}
