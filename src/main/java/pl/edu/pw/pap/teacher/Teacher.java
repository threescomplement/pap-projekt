package pl.edu.pw.pap.teacher;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Teacher  {


    @Id
    @GeneratedValue
    private Long id;
    private String name;


    public Teacher(String name) {this.name = name;}

    protected Teacher() {
    }

}
