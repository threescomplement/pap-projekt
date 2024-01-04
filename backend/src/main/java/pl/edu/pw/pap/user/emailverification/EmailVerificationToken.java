package pl.edu.pw.pap.user.emailverification;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.ToString;
import pl.edu.pw.pap.user.User;

import java.time.Instant;

@Entity
@Getter
@ToString
public class EmailVerificationToken {
    @Id
    @GeneratedValue
    private Long id;
    private String token;
    private Instant expires;

    @OneToOne
    private User user;

    protected EmailVerificationToken() {}
    public EmailVerificationToken(String token, Instant expires, User user) {
        this.token = token;
        this.expires = expires;
        this.user = user;
    }

    public boolean isExpired() {
        return expires.isBefore(Instant.now());
    }
}
