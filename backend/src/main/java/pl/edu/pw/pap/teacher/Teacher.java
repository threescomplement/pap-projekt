package pl.edu.pw.pap.teacher;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.course.Course;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@EqualsAndHashCode
public class Teacher {


    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Course> courses = new HashSet<>();

    public Teacher(String name) {
        this.name = name;
    }

    protected Teacher() {
    }

    @PreRemove
    public void preRemove() {
        this.courses.forEach(c -> c.setTeacher(null));
        this.courses.clear();
    }

    public void addCourse(Course course) {
        this.courses.add(course);
        course.setTeacher(this);
    }

    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.setTeacher(null);
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
