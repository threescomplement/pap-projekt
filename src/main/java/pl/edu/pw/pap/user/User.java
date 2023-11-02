package pl.edu.pw.pap.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private Long id;
    private String username;
    @JsonIgnore
    private String password;
    private String role;
}
