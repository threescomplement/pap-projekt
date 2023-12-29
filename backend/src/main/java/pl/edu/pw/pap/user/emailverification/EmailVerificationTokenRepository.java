package pl.edu.pw.pap.user.emailverification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenEquals(String token);

    Optional<EmailVerificationToken> findByUser_Username(String username);
}
