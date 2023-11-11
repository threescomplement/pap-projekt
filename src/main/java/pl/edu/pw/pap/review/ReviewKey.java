package pl.edu.pw.pap.review;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@ToString
class ReviewKey implements Serializable {


    @Column(name = "user_id")
    Long userId;

    @Column(name = "course_id")
    Long courseId;

    public ReviewKey(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
    }

    protected ReviewKey() {

    }
}
