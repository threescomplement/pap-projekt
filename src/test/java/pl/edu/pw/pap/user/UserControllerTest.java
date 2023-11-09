package pl.edu.pw.pap.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static net.bytebuddy.matcher.ElementMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController
 * <p>
 * The objective of unit tests is to test the class in isolation.
 * </p>
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    /**
     * Allows for mocking HTTP requests to the controller and test its responses
     */
    @Autowired
    private MockMvc api;

    /**
     * Allow explicit converting of objects to JSON
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * `@MockBean` annotation will cause Spring not to inject the actual `UserService` object
     * into the UserController bean, instead the Mockito library allows us to redefine its behavior
     * for the purpose of a single test. Unit tests should verify behavior of UserController in isolation,
     * not its dependencies
     */
    @MockBean
    private UserService userService;



    @Test
    public void registerNewUser() throws Exception {
        var request = new RegisterRequest("rdeckard", "rdeckard@example.com", "password");
        var json = objectMapper.writeValueAsString(request);
        var user = new User(
                "rdeckard",
                "rdeckard@example.com",
                "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2",
                "ROLE_USER",
                false
        );

        // Define behavior of mocked object
        Mockito.doReturn(user).when(userService).registerNewUser(request);

        // Perform a mock HTTP request and check response status code and its contents
        api.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(user.getUsername()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.enabled").value(user.getEnabled()))
                .andExpect(jsonPath("$.role").value(user.getRole()));
    }

    @Test
    public void registerUsernameAlreadyUsed() throws Exception {
        var request = new RegisterRequest("rdeckard", "rdeckard@example.com", "password");
        var json = objectMapper.writeValueAsString(request);

        Mockito.doThrow(new UserRegistrationException("Username taken")).when(userService).registerNewUser(request);

        api.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Username taken"));
    }

    @Test
    public void registerEmailAlreadyUsed() throws Exception {
        var request = new RegisterRequest("rdeckard", "rdeckard@example.com", "password");
        var json = objectMapper.writeValueAsString(request);

        Mockito.doThrow(new UserRegistrationException("Email already used")).when(userService).registerNewUser(request);

        api.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email already used"));
    }
}