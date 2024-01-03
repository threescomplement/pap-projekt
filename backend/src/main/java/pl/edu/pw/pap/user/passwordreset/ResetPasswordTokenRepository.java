package pl.edu.pw.pap.user.passwordreset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken, Long> {
    public Optional<ResetPasswordToken> findByToken(String token);

    @Query("SELECT rpt FROM ResetPasswordToken rpt WHERE rpt.expires < CURRENT_TIMESTAMP")
    List<ResetPasswordToken> findAllExpired();
}
