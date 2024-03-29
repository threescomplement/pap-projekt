package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.ForbiddenException;
import pl.edu.pw.pap.config.AppConfiguration;
import pl.edu.pw.pap.email.EmailSender;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.emailverification.EmailVerificationException;
import pl.edu.pw.pap.user.emailverification.EmailVerificationToken;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;
import pl.edu.pw.pap.user.passwordchange.PasswordChangeException;
import pl.edu.pw.pap.user.passwordreset.PasswordResetException;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordToken;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
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

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<UserDTO> findByUsernameDTO(String username) {
        return findByUsername(username)
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

    public void deleteUser(String username, UserPrincipal principal) {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with username %s does not exist", username)));

        if (!principal.getUserId().equals(user.getId()) && !principal.isAdmin()) {
            throw new ForbiddenException("You are not permitted to delete this account");
        }

        userRepository.delete(user);
    }

    /**
     * Only admin can modify the user record directly
     * Normal users can change some of their account's properties via specific endpoints (reset password, confirm email etc.)
     * @return DTO of updated user
     */
    public UserDTO updateUser(String username, UpdateUserRequest request, UserPrincipal principal) {
        if (!principal.isAdmin()) {
            throw new ForbiddenException("Only allowed for administrators");
        }

        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with username %s does not exist", username)));

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setRole(request.role());
        user.setEnabled(request.enabled());
        return convertToDto(userRepository.save(user));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public void changePassword(UserPrincipal principal, String oldPassword, String newPassword) {
        var user = userRepository.findByUsername(principal.getUsername())
                .orElseThrow(() -> new UserNotFoundException(String.format("User %s does not exist", principal.getUsername())));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new PasswordChangeException("Incorrect old password");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
