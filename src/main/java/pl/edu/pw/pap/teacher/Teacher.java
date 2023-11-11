package pl.edu.pw.pap.teacher;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.edu.pw.pap.course.Course;

import java.util.Set;

@Entity
@Getter
@Setter
@ToString
public class Teacher  {


    @Id
    @GeneratedValue
    private Long id;
    private String name;
    @OneToMany(mappedBy = "teacher")
    private Set<Course> courses;

    public Teacher(String name) {this.name = name;}

    protected Teacher() {
    }

}
