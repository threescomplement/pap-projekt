package pl.edu.pw.pap.user;

import com.jayway.jsonpath.JsonPath;
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
import pl.edu.pw.pap.security.AuthService;
import pl.edu.pw.pap.user.emailverification.EmailVerificationRequest;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordRequest;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordToken;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordTokenRepository;
import pl.edu.pw.pap.user.passwordreset.SendResetPasswordEmailRequest;
import pl.edu.pw.pap.utils.DummyData;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private AuthService authService;
    @Autowired
    private DummyData data;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    public void clearDatabase() {
        data.deleteAll();
        headers = new HttpHeaders();
    }

    private void authenticateAsUser(User user) {
        headers = new HttpHeaders();
        var token = authService.attemptLogin(user.getUsername(), "password").getAccessToken();
        headers.add("Authorization", "Bearer " + token);
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

    @Test
    public void resetPasswordWithExpiredToken() {
        var user = new User(
                "user_1",
                "user@example.com",
                passwordEncoder.encode("password"),
                "ROLE_USER",
                true
        );
        userRepository.save(user);

        passwordTokenRepository.save(
                ResetPasswordToken.builder()
                        .expires(Instant.now().minus(1L, ChronoUnit.DAYS))
                        .email(user.getEmail())
                        .token("token")
                        .build()
        );

        var response = restTemplate.exchange(
                buildUrl("/api/users/reset-password", port),
                HttpMethod.POST,
                new HttpEntity<>(new ResetPasswordRequest("newPassword", "token")),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    public void getUserExists() {
        data.addDummyData();
        authenticateAsUser(data.user_1);

        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());

        var json = JsonPath.parse(response.getBody());
        assertEquals(data.user_1.getId(), Long.parseLong(json.read("$.id").toString()));
        assertEquals(data.user_1.getUsername(), json.read("$.username"));
        assertEquals(data.user_1.getEmail(), json.read("$.email"));
        assertEquals(data.user_1.getRole(), json.read("$.role"));
        assertEquals(data.user_1.getEnabled(), json.read("$.enabled"));

        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/users/" + data.user_1.getUsername()));
        assertTrue(json.read("$._links.reviews.href").toString().endsWith("/api/reviews/" + data.user_1.getUsername()));
        assertTrue(json.read("$._links.comments.href").toString().endsWith(String.format("/api/users/%s/comments", data.user_1.getUsername())));

        data.deleteAll();
    }

    @Test
    public void getUserNotExists() {
        data.addDummyData();
        authenticateAsUser(data.user_1);
        var response = restTemplate.exchange(
                buildUrl("/api/users/idonotexist", port),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    public void updateUserBySelf() {
        data.addDummyData();
        authenticateAsUser(data.user_1);

        var request = new UpdateUserRequest("username", "email@example.com", "ROLE_ADMIN", true);
        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
    }

    @Test
    public void updateUserByAnotherUser() {
        data.addDummyData();
        authenticateAsUser(data.user_2);

        var request = new UpdateUserRequest("username", "email@example.com", "ROLE_ADMIN", true);
        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
    }

    @Test
    public void updateUserByAdmin() {
        data.addDummyData();
        authenticateAsUser(data.admin_1);


        var userBefore = userRepository.findByUsername(data.user_1.getUsername()).get();
        var request = new UpdateUserRequest("newUsername", "newEmail@example.com", "ROLE_ADMIN", true);
        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        var json = JsonPath.parse(response.getBody());
        var userAfter = userRepository.findById(userBefore.getId()).get();

        assertEquals(userBefore.getId(), userAfter.getId());
        assertEquals(userBefore.getId(), Long.valueOf(json.read("$.id").toString()));
        assertEquals(request.username(), userAfter.getUsername());
        assertEquals(request.username(), json.read("$.username"));
        assertEquals(request.email(), userAfter.getEmail());
        assertEquals(request.email(), json.read("$.email"));
        assertEquals(request.role(), userAfter.getRole());
        assertEquals(request.role(), json.read("$.role"));
        assertEquals(request.enabled(), userAfter.getEnabled());
        assertEquals(request.enabled(), json.read("$.enabled"));

        assertTrue(json.read("$._links.self.href").toString().endsWith("/api/users/newUsername"));
        assertTrue(json.read("$._links.reviews.href").toString().endsWith("/api/reviews/newUsername"));
        assertTrue(json.read("$._links.comments.href").toString().endsWith("/api/users/newUsername/comments"));
    }

    @Test
    public void updateUserByAdminNotExists() {
        data.addDummyData();
        authenticateAsUser(data.admin_1);
        var request = new UpdateUserRequest("newUsername", "newEmail@example.com", "ROLE_ADMIN", true);
        var response = restTemplate.exchange(
                buildUrl("/api/users/idonotexist", port),
                HttpMethod.PUT,
                new HttpEntity<>(request, headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }

    @Test
    public void deleteUserBySelf() {
        data.addDummyData();
        authenticateAsUser(data.user_1);
        assertTrue(userRepository.existsUserByUsername(data.user_1.getUsername()));

        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        assertFalse(userRepository.existsUserByUsername(data.user_1.getUsername()));
    }

    @Test
    public void deleteUserByOtherUser() {
        data.addDummyData();
        authenticateAsUser(data.user_2);
        assertTrue(userRepository.existsUserByUsername(data.user_1.getUsername()));

        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(403), response.getStatusCode());
        assertTrue(userRepository.existsUserByUsername(data.user_1.getUsername()));
    }

    @Test
    public void deleteUserByAdmin() {
        data.addDummyData();
        authenticateAsUser(data.admin_1);
        assertTrue(userRepository.existsUserByUsername(data.user_1.getUsername()));

        var response = restTemplate.exchange(
                buildUrl("/api/users/" + data.user_1.getUsername(), port),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(204), response.getStatusCode());
        assertFalse(userRepository.existsUserByUsername(data.user_1.getUsername()));
    }

    @Test
    public void deleteUserByAdminNotExists() {
        data.addDummyData();
        authenticateAsUser(data.user_1);

        var response = restTemplate.exchange(
                buildUrl("/api/users/idonotexist", port),
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                String.class
        );
        assertEquals(HttpStatusCode.valueOf(400), response.getStatusCode());
    }
}
