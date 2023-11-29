package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.UserController;

import javax.swing.text.html.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/courses/{courseId}/reviews/{reviewerId}/comments")
    public CollectionModel<Comment> getCommentsForReview(@PathVariable Long courseId, @PathVariable Long reviewerId) {
        var comments = commentService.getCommentsForReview(courseId, reviewerId);
        return CollectionModel.of(
                comments,
                linkTo(methodOn(CommentController.class).getCommentsForReview(courseId, reviewerId)).withSelfRel()
        );
    }

    @GetMapping("/api/comments/{commentId}")
    public EntityModel<Comment> getCommentById(@PathVariable Long commentId) {
        Optional<Comment> maybecomment = commentService.findCommentById(commentId);
        if (maybecomment.isEmpty()) {
            return null; // TODO: learn how the fuck to handle exceptions properly
        }
        Comment comment = maybecomment.get();
        Link selfLink = linkTo(methodOn(CommentController.class).getCommentById(commentId)).withSelfRel();
        // Todo: create a link to a getReview action
//        Link reviewLink = linkTo ReviewController.getReview(comment.getReview
        Link userLink = linkTo(methodOn(UserController.class).getUser(comment.getUser().getUsername())).withRel("user");
        Link[] links = {selfLink, userLink};
        return EntityModel.of(comment, links);
    }

    @GetMapping("/api/users/{username}/comments")
    public CollectionModel<EntityModel<Comment>> getUserComments(@PathVariable String username){
        List<Comment> comments = commentService.getCommentsByUsername(username);
        List<EntityModel<Comment>> commentModelList = new ArrayList<>();
        for (Comment comment: comments) {
            commentModelList.add(getCommentById(comment.getId()));
        }
        return CollectionModel.of(
                commentModelList,
                linkTo(methodOn(UserController.class).getUser(username)).withRel("user")
        );
    }



    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Comment> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(commentId, principal);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(value = commentNotFoundException.class)
    public ResponseEntity<Exception> handleCommentNotFound(Exception e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);}
}
