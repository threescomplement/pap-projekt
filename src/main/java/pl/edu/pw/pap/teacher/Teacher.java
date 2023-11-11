package pl.edu.pw.pap.teacher;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.course.Course;

import java.util.Set;

@Entity
@Getter
@Setter
public class Teacher {


    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @OneToMany(mappedBy = "teacher", fetch = FetchType.EAGER)
    private Set<Course> courses;

    public Teacher(String name) {
        this.name = name;
    }

    protected Teacher() {
    }

    @Override
    public String toString() {
        return "Teacher{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
