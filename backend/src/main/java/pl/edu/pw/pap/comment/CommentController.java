package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.*;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.review.ReviewNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.UserController;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/courses/{courseId}/reviews/{username}/comments")
    public RepresentationModel<EntityModel<Comment>> getCommentsForReview(@PathVariable Long courseId, @PathVariable String username) {
        var comments = commentService.getCommentsForReview(courseId, username);

        List<EntityModel<Comment>> commentModelList = comments.stream()
                .map(this::addLinks)
                .toList();

        return HalModelBuilder.emptyHalModel()
                .embed(commentModelList.isEmpty() ? Collections.emptyList() : commentModelList, LinkRelation.of("comments"))
                .link(linkTo(methodOn(CommentController.class).getCommentsForReview(courseId, username)).withSelfRel())
                .build();
    }

    @GetMapping("/api/comments/{commentId}")
    public EntityModel<Comment> getCommentById(@PathVariable Long commentId) {
        var comment = commentService.findCommentById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("no comment with ID: " + commentId));
        return addLinks(comment);
    }

    @GetMapping("/api/users/{username}/comments")
    public CollectionModel<EntityModel<Comment>> getUserComments(@PathVariable String username) {
        List<Comment> comments = commentService.getCommentsByUsername(username);
        List<EntityModel<Comment>> commentModelList = comments.stream()
                .map(this::addLinks)
                .toList();
//        List<EntityModel<Comment>> commentModelList = new ArrayList<>();
//        for (Comment comment : comments) {
//            commentModelList.add(getCommentById(comment.getId()));
//        }
        return CollectionModel.of(
                commentModelList,
                linkTo(methodOn(UserController.class).getUser(username)).withRel("user") // TODO fix empty list handling
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

    @ExceptionHandler({CommentNotFoundException.class, UserNotFoundException.class, ReviewNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Exception> handleUnauthorized(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }

    private EntityModel<Comment> addLinks(Comment comment) {
        return EntityModel.of(
                comment,
                linkTo(methodOn(CommentController.class).getCommentById(comment.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getReview(comment.getReview().getCourse().getId(), comment.getUser().getUsername())).withRel("review"),
                linkTo(methodOn(UserController.class).getUser(comment.getUser().getUsername())).withRel("user")
        );
    }
}