package pl.edu.pw.pap;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.user.emailverification.EmailVerificationToken;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;

@Service
@RequiredArgsConstructor
public class ScheduledTasksService {
    private static final Logger log = LoggerFactory.getLogger(ScheduledTasksService.class);
    private final EmailVerificationTokenRepository emailTokenRepository;

    @Scheduled(cron = "*/5 * * * * *") //TODO load cron expression from properties
    public void deleteExpiredEmailVerificationTokens() {
        var expiredTokens = emailTokenRepository.findAll().stream()
                        .filter(EmailVerificationToken::isExpired)
                        .toList();
        if (!expiredTokens.isEmpty()) {
            log.info(String.format("Found %d expired email verification tokens: %s", expiredTokens.size(), expiredTokens));
            emailTokenRepository.deleteAll(expiredTokens);
            log.info(String.format("Deleted %d tokens", expiredTokens.size()));
        }
    }
}
