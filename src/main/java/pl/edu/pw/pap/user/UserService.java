package pl.edu.pw.pap.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final String DUMMY_USERNAME = "rdeckard";
    public Optional<User> findByUsername(String username) {
        // TODO: use database repository
        if (!DUMMY_USERNAME.equals(username)) {
            return Optional.empty();
        }

        var user = new User();
        user.setId(1L);
        user.setUsername(DUMMY_USERNAME);
        user.setPassword("$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2"); // password
        user.setRole("ADMIN");
        return Optional.of(user);
    }
}
