package pl.edu.pw.pap.user.passwordreset;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Entity
@Builder
@Data
public class ResetPasswordToken {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private String email;

    public ResetPasswordToken() {

    }
}
