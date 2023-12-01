package pl.edu.pw.pap.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.teacher.Teacher;

import java.util.Set;

@Entity
@Getter
@Setter
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String language;
    private String type;
    private String level;
    private String module;

    @ManyToOne
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "course")
    private Set<Review> reviews;

    public Course(String name, String language, String type, String level, String module, Teacher teacher) {
        this.name = name;
        this.language = language;
        this.type = type;
        this.level = level;
        this.module = module;
        this.teacher = teacher;
    }


    public void removeReview (Review review){
        this.reviews.remove(review);
    }
    protected Course() {

    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", language='" + language + '\'' +
                ", type='" + type + '\'' +
                ", level='" + level + '\'' +
                ", module='" + module + '\'' +
                ", teacher=" + teacher.getId() +
                '}';
    }
}
