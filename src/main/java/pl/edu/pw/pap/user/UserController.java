package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/api/users")
    public User registerNewUser(@RequestBody RegisterRequest request) {
        return userService.registerNewUser(request);
    }

    @PostMapping("/api/users/verify")
    public User verifyEmail(@RequestBody String token) {
        return userService.verifyEmailWithToken(token);
    }
}
