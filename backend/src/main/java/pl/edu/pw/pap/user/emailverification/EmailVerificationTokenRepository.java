package pl.edu.pw.pap.user.emailverification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    Optional<EmailVerificationToken> findByTokenEquals(String token);

    Optional<EmailVerificationToken> findByUser_Username(String username);

    @Query("SELECT evt FROM EmailVerificationToken evt WHERE evt.expires < CURRENT_TIMESTAMP")
    List<EmailVerificationToken> findAllExpired();
}
