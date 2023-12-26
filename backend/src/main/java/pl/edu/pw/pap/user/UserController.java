package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.user.emailverification.EmailVerificationRequest;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public User verifyEmail(@RequestBody EmailVerificationRequest request) {
        log.info("Attempting to verify email with token " + request.token());
        return userService.verifyEmailWithToken(request.token());
    }

//    @PostMapping("/api/users/send-reset-email")
//    public ResponseEntity<String> sendPasswordResetEmail(@RequestBody ResetPasswordEmailRequest request) {
//        try {
//            userService.sendPasswordResetEmail(request.email());
//            return ResponseEntity.ok("Sent email with link to reset your password");
//        } catch (UserNotFoundException e) {
//            return ResponseEntity.badRequest().body(String.format("User with email %s does not exist", request.email()));
//        }
//    }
//
//    @PostMapping("/api/users/reset-password")
//    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
//        try {
//            userService.resetPassword(request.newPassword(), request.passwordResetToken());
//            return ResponseEntity.ok("Password has been reset successfully");
//        } catch (Exception e ) {  // TODO proper exception
//            return ResponseEntity.badRequest().body(e.toString());
//        }
//    }

    @GetMapping("/api/users/{username}")
    public EntityModel<User> getUser(@PathVariable String username){
        Link selfLink = linkTo(methodOn(UserController.class).getUser(username)).withSelfRel();
        Link reviewsLink = linkTo(methodOn(ReviewController.class).getUserReviews(username)).withRel("reviews");
        Link commentsLink = linkTo(methodOn(CommentController.class).getUserComments(username)).withRel("comments");
        Link[] links = {selfLink, reviewsLink, commentsLink};
        return EntityModel.of(
                userService.findByUsername(username).orElseThrow(),
                links
        );
    }

    @ExceptionHandler(value = UserRegistrationException.class)
    public ResponseEntity<Exception> handleUsernameTaken(Exception e) {
        return ResponseEntity.badRequest().body(e);
    }
}
