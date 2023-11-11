package pl.edu.pw.pap.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.teacher.Teacher;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name="teacher_id")
    private Teacher teacher;


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "course")
    private Set<Review> reviews;

    public Course(String name) {
        this.name = name;

    }

    protected Course() {

    }
}
