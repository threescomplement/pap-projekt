package pl.edu.pw.pap.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.review.report.ReviewReport;
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

    private String opinion;
    private Boolean edited;
    private int overallRating; // TODO: Decide which parameters should be included in the review
    @CreationTimestamp
    private Timestamp created;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Comment> comments = new HashSet<>();

    @JsonIgnore // Don't want to access reports when simply asking for review
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewReport> reports = new HashSet<>();

    public Review(User user, Course course, String opinion, int overallRating) {
        this.opinion = opinion;
        this.overallRating = overallRating;
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
        this.reports.forEach(r -> r.setReported(null));
        this.reports.clear();
    }

    public void removeComment(Comment comment) {
        this.comments.remove(comment);
        comment.setReview(null);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        comment.setReview(this);
    }

    public void removeReport(ReviewReport report) {
        this.reports.remove(report);
        report.setReported(null);
    }

    public void addReport(ReviewReport report) {
        this.reports.add(report);
        report.setReported(this);
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", course=" + course.getName() +
                ", opinion='" + opinion + '\'' +
                ", overallRating=" + overallRating +
                ", edited='" + edited +
                ", created=" + created.toString() +
                '}';
    }

    protected Review() {
    }
}
