package pl.edu.pw.pap.course;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pw.pap.review.Review;

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

    @OneToMany(mappedBy = "course")
    private Set<Review> reviews;

    public Course(String name) {
        this.name = name;
    }

    protected Course() {

    }
}
