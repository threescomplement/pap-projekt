package pl.edu.pw.pap.teacher;

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
public class Teacher  {


    @Id
    @GeneratedValue
    private Long id;
    private String name;


    public Teacher(String name) {this.name = name;}

    protected Teacher() {
    }

}
