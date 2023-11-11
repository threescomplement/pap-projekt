package pl.edu.pw.pap.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.comment.Comment;

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
    private int likes;
    private int dislikes;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Comment> comments;

    public Review(User user, Course course, String opinion, int overallRating) {
        this.user = user;
        this.course = course;
        this.opinion = opinion;
        this.overallRating = overallRating;
        this.likes = 0;
        this.dislikes = 0;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", user=" + user.getUsername() +
                ", course=" + course.getName() +
                ", opinion='" + opinion + '\'' +
                ", overallRating=" + overallRating +
                ", likes=" + likes +
                ", dislikes=" + dislikes +
                '}';
    }

    protected Review() {
    }
}
