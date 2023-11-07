package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String DEFAULT_ROLE = "ROLE_USER";

    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository tokenRepository;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User registerNewUser(RegisterRequest request) throws EmailTakenException, UsernameTakenException {
        if (userRepository.existsUserByEmail(request.email())) {
            throw new EmailTakenException();
        }

        if (userRepository.existsUserByUsername(request.username())) {
            throw new UsernameTakenException();
        }

        var user = new User(request.username(), request.email(), request.password(), DEFAULT_ROLE, false);
        user = userRepository.save(user);
        generateVerificationToken(user);
        return user;
    }

    private void generateVerificationToken(User user) {
        var token = new EmailVerificationToken(
                UUID.randomUUID().toString(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                user
        );

        tokenRepository.save(token);
    }

    public User verifyEmailWithToken(String token) {
        var verificationToken = tokenRepository.findByToken(token)
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
