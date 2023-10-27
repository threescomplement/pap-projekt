package pl.edu.pw.pap.course;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    private String name;

    public Course(String name) {
        this.name = name;
    }

    protected Course() {

    }
}
