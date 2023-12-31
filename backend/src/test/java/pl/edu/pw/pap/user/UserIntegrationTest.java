package pl.edu.pw.pap.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.pap.PapApplication;
import pl.edu.pw.pap.email.EmailSender;
import pl.edu.pw.pap.user.emailverification.EmailVerificationRequest;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordRequest;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordTokenRepository;
import pl.edu.pw.pap.user.passwordreset.SendResetPasswordEmailRequest;
import pl.edu.pw.pap.utils.DummyData;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static pl.edu.pw.pap.utils.UrlBuilder.buildUrl;

/**
 * Integration tests for User-related functionality
 * <p>
 * The purpose of integration test is to verify that all components interact correctly.
 * This test starts the entire Spring Application, connects to the database and performs network requests.
 * </p>
 */
@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    @MockBean
    private EmailSender fakeEmailSender;  // Do not try to send actual emails

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailVerificationTokenRepository tokenRepository;
    @Autowired
    private ResetPasswordTokenRepository passwordTokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DummyData dummyData;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    public void clearDatabase() {
        dummyData.deleteAll();
    }

    @Test
    public void registerNewUser() throws Exception {
        var request = new RegisterRequest("new_user", "new_user@example.com", "password");
        HttpEntity<RegisterRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<User> response = restTemplate.exchange(
                buildUrl("/api/users", port),
                HttpMethod.POST, entity, User.class
        );

        var resultUser = response.getBody();
        assertNotNull(resultUser);
        assertEquals("new_user", resultUser.getUsername());
        assertEquals("new_user@example.com", resultUser.getEmail());
        assertNull(resultUser.getPassword());
        assertEquals("ROLE_USER", resultUser.getRole());
        assertFalse(resultUser.getEnabled());

        var dbUser = userRepository.findByUsername(request.username()).orElseThrow();
        assertNotNull(dbUser);
        assertEquals("new_user", dbUser.getUsername());
        assertEquals("new_user@example.com", dbUser.getEmail());
        assertNotNull(dbUser.getPassword());  // TODO how to check it properly?
        assertEquals("ROLE_USER", dbUser.getRole());
        assertFalse(dbUser.getEnabled());
    }

    @Test
    public void verifyEmail() throws Exception {
        // Register new user
        var registerRequest = new RegisterRequest("new_user", "new_user@example.com", "password");
        HttpEntity<RegisterRequest> registerEntity = new HttpEntity<>(registerRequest, headers);
        ResponseEntity<User> response = restTemplate.exchange(
                buildUrl("/api/users", port),
                HttpMethod.POST, registerEntity, User.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        // Use verification token sent by email
        var verificationToken = tokenRepository.findByUser_Username(registerRequest.username()).get();
        var verifyRequest = new EmailVerificationRequest(verificationToken.getToken());
        var verifyEntity = new HttpEntity<>(verifyRequest, headers);
        var verifyResponse = restTemplate.exchange(
                buildUrl("/api/users/verify", port),
                HttpMethod.POST, verifyEntity, User.class
        );

        // Check if user is enabled
        assertEquals(HttpStatusCode.valueOf(200), verifyResponse.getStatusCode());
        assertTrue(verifyResponse.getBody().getEnabled());
        assertTrue(userRepository.findByUsername(registerRequest.username()).get().getEnabled());

    }

    @Test
    public void resetPasswordSuccess() throws Exception {
        // Create user
        var user = new User(
                "user_1",
                "user@example.com",
                passwordEncoder.encode("password"),
                "ROLE_USER",
                true
        );
        userRepository.save(user);

        // Request email to be sent
        var sendEmailResponse = restTemplate.exchange(
                buildUrl("/api/users/send-reset-email", port),
                HttpMethod.POST,
                new HttpEntity<>(new SendResetPasswordEmailRequest("user@example.com"), headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), sendEmailResponse.getStatusCode());

        // Check if token was generated
        var token = passwordTokenRepository.findAll()
                .stream()
                .filter(t -> t.getEmail().equals(user.getEmail()))
                .findFirst()
                .get();

        // Check that email was supposed to be sent with correct parameters
        verify(fakeEmailSender).sendEmail(eq("user@example.com"), contains(token.getToken()), eq("Reset your password"));

        // Attempt to change the password
        var resetPasswordResponse = restTemplate.exchange(
                buildUrl("/api/users/reset-password", port),
                HttpMethod.POST,
                new HttpEntity<>(new ResetPasswordRequest("newPassword", token.getToken())),
                String.class
        );

        // Check if password was updated
        assertEquals(HttpStatusCode.valueOf(200), resetPasswordResponse.getStatusCode());
        var updatedUser = userRepository.findById(user.getId()).get();
        assertTrue(passwordEncoder.matches("newPassword", updatedUser.getPassword()));

        // Check if token was deleted afterwards
        assertFalse(passwordTokenRepository.existsById(token.getId()));
    }

    @Test
    public void sendResetPasswordEmailWithIncorrectEmail() {
        var response = restTemplate.exchange(
                buildUrl("/api/users/send-reset-email", port),
                HttpMethod.POST,
                new HttpEntity<>(new SendResetPasswordEmailRequest("iDoNotExist@example.com"), headers),
                String.class
        );

        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    public void resetPasswordWithInvalidToken() {
        var response = restTemplate.exchange(
                buildUrl("/api/users/reset-password", port),
                HttpMethod.POST,
                new HttpEntity<>(new ResetPasswordRequest("newPassword", UUID.randomUUID().toString())),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    //TODO reset password expired token
}
