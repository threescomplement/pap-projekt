package pl.edu.pw.pap.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO extends RepresentationModel<UserDTO> {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Boolean enabled;
}
