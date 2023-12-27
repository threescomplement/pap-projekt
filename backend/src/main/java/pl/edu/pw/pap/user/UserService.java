package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.email.EmailSender;
import pl.edu.pw.pap.email.EmailSenderProperties;
import pl.edu.pw.pap.user.emailverification.EmailVerificationException;
import pl.edu.pw.pap.user.emailverification.EmailVerificationToken;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordToken;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordTokenRepository;

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
    private final EmailVerificationTokenRepository emailTokenRepository;
    private final ResetPasswordTokenRepository passwordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final EmailSenderProperties emailProperties;

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
        sendVerificationEmail(user);
        return user;
    }

    public EmailVerificationToken generateVerificationToken(User user) {
        var token = new EmailVerificationToken(
                UUID.randomUUID().toString(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                user
        );

        return emailTokenRepository.save(token);
    }

    public User verifyEmailWithToken(String token) {
        emailTokenRepository.findAll().forEach(t -> log.info(t.toString()));


        var verificationToken = emailTokenRepository.findByTokenEquals(token)
                .orElseThrow(() -> new EmailVerificationException("Verification token not found"));

        if (verificationToken.isExpired()) {
            throw new EmailVerificationException("Verification token expired");
        }

        var user = verificationToken.getUser();
        user.setEnabled(true);
        user = userRepository.save(user);
        emailTokenRepository.delete(verificationToken);
        return user;
    }

    private void sendVerificationEmail(User user) {
        var token = generateVerificationToken(user);
        emailSender.sendEmail(
                user.getEmail(),
                String.format("Click here to confirm your email: %s%s", emailProperties.getConfirmBaseUrl(), token.getToken()),
                "Verify your email"
        );
    }


    public void sendPasswordResetEmail(String email) {
        var token = generatePasswordResetToken(email);
        emailSender.sendEmail(
                email,
                String.format("Click here to reset your password: %s%s", emailProperties.getResetPasswordBaseUrl(), token.getToken()),
                "Reset your password"
        );
    }

    public void resetPassword(String passwordTokenStr, String newPassword) {
        var resetToken = passwordTokenRepository.findByToken(passwordTokenStr)
                .orElseThrow();

        var user = userRepository.findByEmail(resetToken.getEmail())
                .orElseThrow();

        var encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
        passwordTokenRepository.delete(resetToken);
    }

    private ResetPasswordToken generatePasswordResetToken(String email) throws UserNotFoundException {
        if (!userRepository.existsUserByEmail(email)) {
            throw new UserNotFoundException(String.format("User with email %s does not exist", email));
        }

        return passwordTokenRepository.save(
                ResetPasswordToken.builder()
                        .token(UUID.randomUUID().toString())
                        .email(email)
                        .build()
        );
    }
}
