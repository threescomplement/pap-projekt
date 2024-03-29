package pl.edu.pw.pap.user;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.ForbiddenException;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.emailverification.EmailVerificationRequest;
import pl.edu.pw.pap.user.passwordchange.ChangePasswordRequest;
import pl.edu.pw.pap.user.passwordreset.ResetPasswordRequest;
import pl.edu.pw.pap.user.passwordreset.SendResetPasswordEmailRequest;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    @PostMapping("/api/users")
    public UserDTO registerNewUser(@RequestBody RegisterRequest request) {
        return addLinks(
                userService.registerNewUser(request)
        );
    }

    @GetMapping("/api/users/{username}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String username) {
        return userService
                .findByUsernameDTO(username)
                .map(this::addLinks)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/users")
    public RepresentationModel<UserDTO> getAllUsers() {
        var users = userService.getAllUsers().stream()
                .map(this::addLinks)
                .toList();

        return HalModelBuilder.emptyHalModel()
                .embed(users.isEmpty() ? Collections.emptyList() : users, LinkRelation.of("users"))
                .build();
    }


    @PutMapping("/api/users/{username}")
    public ResponseEntity<UserDTO> updateUser(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String username, @RequestBody UpdateUserRequest request) {
        try {
            var user = userService.updateUser(username, request, principal);
            return ResponseEntity.ok(addLinks(user));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }


    @DeleteMapping("/api/users/{username}")
    public ResponseEntity<String> deleteUser(@AuthenticationPrincipal UserPrincipal principal, @PathVariable String username) {
        try {
            userService.deleteUser(username, principal);
            return ResponseEntity.noContent().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().build();
        } catch (ForbiddenException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/api/users/verify")
    public UserDTO verifyEmail(@RequestBody EmailVerificationRequest request) {
        return addLinks(
                userService.verifyEmailWithToken(request.token())
        );
    }

    @PostMapping("/api/users/send-reset-email")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestBody SendResetPasswordEmailRequest request) {
        try {
            userService.sendPasswordResetEmail(request.email());
            return ResponseEntity.ok("Sent email with link to reset your password");
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(String.format("User with email %s does not exist", request.email()));
        }
    }

    @PostMapping("/api/users/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            userService.resetPassword(request.passwordResetToken(), request.newPassword());
            return ResponseEntity.ok("Password has been reset successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }

    @PostMapping("/api/users/change-password")
    public ResponseEntity<String> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody ChangePasswordRequest request
    ) {
        try {
            userService.changePassword(principal, request.oldPassword(), request.newPassword());
            return ResponseEntity.ok("Password has been changed successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.toString());
        }
    }


    @ExceptionHandler(value = UserRegistrationException.class)
    public ResponseEntity<Exception> handleUsernameTaken(Exception e) {
        return ResponseEntity.badRequest().body(e);
    }

    private UserDTO addLinks(UserDTO user) {
        return user.add(
                linkTo(methodOn(UserController.class).getUser(user.getUsername())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getUserReviews(user.getUsername())).withRel("reviews"),
                linkTo(methodOn(CommentController.class).getUserComments(user.getUsername())).withRel("comments")
        );
    }
}
