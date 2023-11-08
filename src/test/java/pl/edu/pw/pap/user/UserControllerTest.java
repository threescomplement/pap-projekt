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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc api;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void registerNewUser() throws Exception {
        var request = new RegisterRequest("rdeckard", "rdeckard@example.com", "password");
        var user = new User(
                "rdeckard",
                "rdeckard@example.com",
                "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2",
                "ROLE_USER",
                false
        );
        var json = objectMapper.writeValueAsString(request);

        Mockito.doReturn(user).when(userService).registerNewUser(request);

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