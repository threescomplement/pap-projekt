package pl.edu.pw.pap.course;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.teacher.Teacher;

import java.util.HashSet;
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

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "course", cascade = CascadeType.MERGE, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    public Course(String name, String language, String type, String level, String module, Teacher teacher) {
        this.name = name;
        this.language = language;
        this.type = type;
        this.level = level;
        this.module = module;
        teacher.addCourse(this);
    }


    protected Course() {

    }

    @PreRemove
    public void preRemove() {
        if (this.teacher != null) {
            this.teacher.removeCourse(this);
        }

        this.reviews.forEach(r -> r.setCourse(null));
        this.reviews.clear();
    }

    public void addReview(Review review) {
        this.reviews.add(review);
        review.setCourse(this);
    }

    public void removeReview(Review review) {
        this.reviews.remove(review);
        review.setCourse(null);
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
