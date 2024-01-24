package pl.edu.pw.pap.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.user.User;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;


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

    @Column(length = 1024)
    private String opinion;
    private int easeRating;
    private int interestRating;
    private int engagementRating;
    private Boolean edited;
    @CreationTimestamp
    private Timestamp created;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    //TODO: Keep a set of reports for a given review

    public Review(User user, Course course, String opinion, int easeRating, int interestingnessRating, int engagementRating) {
        this.opinion = opinion;
        this.easeRating = easeRating;
        this.engagementRating = engagementRating;
        this.interestRating = interestingnessRating;
        edited = false;
        course.addReview(this);
        user.addReview(this);
    }

    @PreRemove
    public void preRemove() {
        if (this.user != null) {
            this.user.removeReview(this);
        }
        if (this.course != null) {
            this.course.removeReview(this);
        }
        this.comments.forEach(c -> c.setReview(null));
        this.comments.clear();
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setReview(null);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setReview(this);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", course=" + course.getName() +
                ", opinion='" + opinion + '\'' +
                ", easeRating=" + easeRating +
                ", interestRating=" + interestRating +
                ", engagementRating=" + engagementRating +
                ", edited='" + edited +
                ", created=" + created.toString() +
                '}';
    }

    protected Review() {
    }
}
