package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.review.reviewNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.UserController;
import pl.edu.pw.pap.user.userNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/courses/{courseId}/reviews/{username}/comments")
    public CollectionModel<EntityModel<Comment>> getCommentsForReview(@PathVariable Long courseId, @PathVariable String username) {
        var comments = commentService.getCommentsForReview(courseId, username);
        List<EntityModel<Comment>> commentModelList = new ArrayList<>();
        for (Comment comment : comments) {
            commentModelList.add(getCommentById(comment.getId()));
        }
        return CollectionModel.of(
                commentModelList,
                linkTo(methodOn(CommentController.class).getCommentsForReview(courseId, username)).withSelfRel()
        );
    }

    @GetMapping("/api/comments/{commentId}")
    public EntityModel<Comment> getCommentById(@PathVariable Long commentId) {
        Optional<Comment> maybecomment = commentService.findCommentById(commentId);
        if (maybecomment.isEmpty()) {
            throw new commentNotFoundException("no comment with ID: " + commentId);
        }
        Comment comment = maybecomment.get();

        Link selfLink = linkTo(methodOn(CommentController.class).getCommentById(commentId)).withSelfRel();
        Link reviewLink = linkTo(methodOn(ReviewController.class).getReview(comment.getReview().getCourse().getId(), comment.getUser().getUsername())).withRel("review");
        Link userLink = linkTo(methodOn(UserController.class).getUser(comment.getUser().getUsername())).withRel("user");
        Link[] links = {selfLink, userLink, reviewLink};
        return EntityModel.of(comment, links);
    }

    @GetMapping("/api/users/{username}/comments")
    public CollectionModel<EntityModel<Comment>> getUserComments(@PathVariable String username) {
        List<Comment> comments = commentService.getCommentsByUsername(username);
        List<EntityModel<Comment>> commentModelList = new ArrayList<>();
        for (Comment comment : comments) {
            commentModelList.add(getCommentById(comment.getId()));
        }
        return CollectionModel.of(
                commentModelList,
                linkTo(methodOn(UserController.class).getUser(username)).withRel("user")
        );
    }

    @PostMapping("/api/courses/{courseId}/reviews/{username}/comments")
    public Comment addComment(@RequestBody AddCommentRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return commentService.addNewComment(request, principal);
    }


    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(commentId, principal);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({commentNotFoundException.class, userNotFoundException.class, reviewNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Exception> handleUnauthorized(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }

}