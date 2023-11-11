package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User registerNewUser(RegisterRequest request) {
        if (userRepository.existsUserByEmail(request.email())) {
            throw new UserRegistrationException("Email address is already being used");
        }

        if (userRepository.existsUserByUsername(request.username())) {
            throw new UserRegistrationException("Username is already being used");
        }

        var user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()), DEFAULT_ROLE, false);
        user = userRepository.save(user);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return user;
    }

    public EmailVerificationToken generateVerificationToken(User user) {
        var token = new EmailVerificationToken(
                UUID.randomUUID().toString(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                user
        );

        return tokenRepository.save(token);
    }

    public User verifyEmailWithToken(String token) {
        tokenRepository.findAll().forEach(t -> log.info(t.toString()));


        var verificationToken = tokenRepository.findByTokenEquals(token)
                .orElseThrow(() -> new EmailVerificationException("Verification token not found"));

        if (verificationToken.isExpired()) {
            throw new EmailVerificationException("Verification token expired");
        }

        var user = verificationToken.getUser();
        user.setEnabled(true);
        user = userRepository.save(user);
        tokenRepository.delete(verificationToken);
        return user;
    }
}
