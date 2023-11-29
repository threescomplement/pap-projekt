package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/api/users")
    public User registerNewUser(@RequestBody RegisterRequest request) {
        return userService.registerNewUser(request);
    }

    @PostMapping("/api/users/verify")
    public User verifyEmail(@RequestBody VerificationRequest request) {
        log.info("Attempting to verify email with token " + request.token());
        return userService.verifyEmailWithToken(request.token());
    }

    @GetMapping("/api/users/{username}")
    public EntityModel<User> getUser(@PathVariable String username){
//         TODO: musi zwracać linki do: getUserReviews, getUserComments
//        Link[] links = {};
        return EntityModel.of(userService.findByUsername(username).orElseThrow());
    }

    @ExceptionHandler(value = UserRegistrationException.class)
    public ResponseEntity<Exception> handleUsernameTaken(Exception e) {
        return ResponseEntity.badRequest().body(e);
    }
}
