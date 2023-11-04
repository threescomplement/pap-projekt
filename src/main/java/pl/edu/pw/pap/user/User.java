package pl.edu.pw.pap.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "app_user")  // Namespace conflict with default value (I guess?)
public class User {
    @Id
    @GeneratedValue
    private Long id;
    private String username;
    private String email;
    @JsonIgnore
    private String password;
    private String role;

    public User(String username, String email, String password, String role) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }


    protected User() {

    }
}
