package pl.edu.pw.pap.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.mail.javamail.JavaMailSender;
import pl.edu.pw.pap.PapApplication;

import static org.junit.jupiter.api.Assertions.*;

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
    private JavaMailSender fakeEmailSender;  // Do not try to send actual emails

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    TestRestTemplate restTemplate = new TestRestTemplate();
    HttpHeaders headers = new HttpHeaders();

    @BeforeEach
    public void clearDatabase() {
        userRepository.deleteAll();
        tokenRepository.deleteAll();
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
        var verifyRequest = new VerificationRequest(verificationToken.getToken());
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

    private static String buildUrl(String endpoint, int port) {
        return "http://localhost:" + port + endpoint;
    }
}
