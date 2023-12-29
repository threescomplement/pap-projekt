package pl.edu.pw.pap.user.passwordreset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    public Optional<ResetPasswordToken> findByToken(String token);
}
