package pl.edu.pw.pap.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.user.User;

import java.io.Serializable;
import java.util.Objects;


class CourseReviewKey implements Serializable {


    @Column(name = "user_id")
    Long userId;

    @Column(name = "course_id")
    Long courseId;

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

    @Id
    @GeneratedValue
    private Long id;

    private User student;

    private Course course;
    private String opinion;
    private int overallRating; // TODO: Decide which paramters should be included in the review

    private int likes;
    private int dislikes;

    public Review(User student, Course course, String opinion, int overallRating) {
        this.student = student;
        this.course = course;
        this.opinion = opinion;
        this.overallRating = overallRating;
        this.likes = 0;
        this.dislikes = 0;
    }

    protected Review() {
    }
}
