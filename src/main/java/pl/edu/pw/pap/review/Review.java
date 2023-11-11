package pl.edu.pw.pap.review;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.user.User;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
class CourseReviewKey implements Serializable {


    @Column(name = "user_id")
    Long userId;

    @Column(name = "course_id")
    Long courseId;

    public CourseReviewKey(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }

    protected CourseReviewKey() {

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CourseReviewKey that = (CourseReviewKey) o;
        return Objects.equals(userId, that.userId) && Objects.equals(courseId, that.courseId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, courseId);
    }
}


@Entity
@Getter
@Setter
public class Review {

    @EmbeddedId
    CourseReviewKey id = new CourseReviewKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id") //currently ignoring the unresolved column, possibly caused by type of our database
    private User user;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id") //currently ignoring the unresolved column, possibly caused by type of our database
    private Course course;
    private String opinion;
    private int overallRating; // TODO: Decide which parameters should be included in the review

    private int likes;
    private int dislikes;

    public Review(User user, Course course, String opinion, int overallRating) {
        this.user = user;
        this.course = course;
        this.opinion = opinion;
        this.overallRating = overallRating;
        this.likes = 0;
        this.dislikes = 0;
    }

    protected Review() {
    }
}
