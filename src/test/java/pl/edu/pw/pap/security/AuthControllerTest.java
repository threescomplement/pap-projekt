package pl.edu.pw.pap.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthControllerTest {
    @Autowired
    private MockMvc api;

    @Test
    void anyoneCanViewPublicEndpoint() throws Exception {
        api.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("Hello, world!")));
    }

    @Test
    void notLoggedInShouldNotSeeSecuredEndpoint() throws Exception {
        api.perform(get("/secured"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "rdeckard")
    void loggedInShouldSeeSecuredEndpoint() throws Exception {
        api.perform(get("/secured"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("You must be logged in to see this! Hello rdeckard!")));
    }

    @Test
    void notLoggedInShouldNotSeeAdminEndpoint() throws Exception {
        api.perform(get("/admin"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void loggedInNonAdminUsersShouldNotSeeAdminEndpoint() throws Exception {
        api.perform(get("/admin"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockAdmin
    void loggedInAdminUsersShouldSeeAdminEndpoint() throws Exception {
        api.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsStringIgnoringCase("user")));
    }

}