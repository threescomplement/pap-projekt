package pl.edu.pw.pap.user;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private static final String DUMMY_ADMIN = "rdeckard";
    private static final String DUMMY_USER = "rbatty";

    public Optional<User> findByUsername(String username) {
        // TODO: use database repository
        if (DUMMY_ADMIN.equalsIgnoreCase(username)) {
            var user = new User();
            user.setId(1L);
            user.setUsername(DUMMY_ADMIN);
            user.setPassword("$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2"); // password
            user.setRole("ROLE_ADMIN");
            return Optional.of(user);
        } else if (DUMMY_USER.equalsIgnoreCase(username)) {
            var user = new User();
            user.setId(2L);
            user.setUsername(DUMMY_USER);
            user.setPassword("$2a$12$ytByi2pSlciOCNJHAf81K.p1YIqZYx7ATiBl/E.4EVlkBqD8k7Uu."); // password2
            user.setRole("ROLE_USER");
            return Optional.of(user);
        }

        return Optional.empty();
    }
}
