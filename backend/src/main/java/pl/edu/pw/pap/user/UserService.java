package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.config.AppConfiguration;
import pl.edu.pw.pap.email.EmailSender;
import pl.edu.pw.pap.user.emailverification.EmailVerificationException;
import pl.edu.pw.pap.user.emailverification.EmailVerificationToken;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;
import pl.edu.pw.pap.user.passwordreset.PasswordResetException;
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

    private final Logger log = LoggerFactory.getLogger(UserService.class);  //TODO proper logging
    private final UserRepository userRepository;
    private final EmailVerificationTokenRepository emailTokenRepository;
    private final ResetPasswordTokenRepository passwordTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailSender emailSender;
    private final AppConfiguration appConfiguration;

    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::convertToDto);
    }

    public UserDTO registerNewUser(RegisterRequest request) {
        if (userRepository.existsUserByEmail(request.email())) {
            throw new UserRegistrationException("Email address is already being used");
        }

        if (userRepository.existsUserByUsername(request.username())) {
            throw new UserRegistrationException("Username is already being used");
        }

        var user = new User(request.username(), request.email(), passwordEncoder.encode(request.password()), DEFAULT_ROLE, false);
        user = userRepository.save(user);
        sendVerificationEmail(user);
        return convertToDto(user);
    }

    public EmailVerificationToken generateVerificationToken(User user) {
        var token = new EmailVerificationToken(
                UUID.randomUUID().toString(),
                Instant.now().plus(1, ChronoUnit.DAYS),
                user
        );

        return emailTokenRepository.save(token);
    }

    public UserDTO verifyEmailWithToken(String token) {
        var verificationToken = emailTokenRepository.findByTokenEquals(token)
                .orElseThrow(() -> new EmailVerificationException("Verification token not found"));

        if (verificationToken.isExpired()) {
            throw new EmailVerificationException("Verification token expired");
        }

        var user = verificationToken.getUser();
        user.setEnabled(true);
        user = userRepository.save(user);
        emailTokenRepository.delete(verificationToken);
        return convertToDto(user);
    }

    private void sendVerificationEmail(User user) {
        var token = generateVerificationToken(user);
        var url = String.format(
                "%s%s%s",
                appConfiguration.getWebsiteBaseUrl(),
                appConfiguration.getConfirmEmailUrl(),
                token.getToken()
        );
        emailSender.sendEmail(
                user.getEmail(),
                String.format("Click here to confirm your email: %s", url),
                "Verify your email"
        );
    }


    public void sendPasswordResetEmail(String email) {
        var token = generatePasswordResetToken(email);
        var url = String.format(
                "%s%s%s",
                appConfiguration.getWebsiteBaseUrl(),
                appConfiguration.getResetPasswordUrl(),
                token.getToken()
        );
        emailSender.sendEmail(
                email,
                String.format("Click here to reset your password: %s", url),
                "Reset your password"
        );
    }

    public void resetPassword(String passwordTokenStr, String newPassword) {
        var resetToken = passwordTokenRepository.findByToken(passwordTokenStr)
                .orElseThrow();

        if (resetToken.isExpired()) {
            throw new PasswordResetException("Token expired");
        }

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
                        .expires(Instant.now().plus(1L, ChronoUnit.DAYS))
                        .build()
        );
    }

    private UserDTO convertToDto(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }
}
