package pl.edu.pw.pap.utils;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordTokenRepository;

@Service
@RequiredArgsConstructor
public class ScheduledTasksService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasksService.class);
    private final EmailVerificationTokenRepository emailTokenRepository;
    private final ResetPasswordTokenRepository passwordTokenRepository;

    @Scheduled(cron = "${app.delete-tokens-cron-expression}")
    public void deleteExpiredEmailVerificationTokens() {
        var expiredTokens = emailTokenRepository.findAllExpired();

        if (!expiredTokens.isEmpty()) {
            log.info(String.format("Found %d expired email verification tokens: %s", expiredTokens.size(), expiredTokens));
            emailTokenRepository.deleteAll(expiredTokens);
            log.info(String.format("Deleted %d tokens", expiredTokens.size()));
        }
    }

    @Scheduled(cron = "${app.delete-tokens-cron-expression}")
    public void deleteExpiredPasswordResetTokens() {
        var expiredTokens = passwordTokenRepository.findAllExpired();

        if (!expiredTokens.isEmpty()) {
            log.info(String.format("Found %d expired password reset tokens: %s", expiredTokens.size(), expiredTokens));
            passwordTokenRepository.deleteAll(expiredTokens);
            log.info(String.format("Deleted %d tokens", expiredTokens.size()));
        }
    }
}
