package pl.edu.pw.pap.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pw.pap.review.Review;

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


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "course")
    private Set<Review> reviews = new HashSet<Review>();

    public Course(String name) {
        this.name = name;

    }

    protected Course() {

    }
}
