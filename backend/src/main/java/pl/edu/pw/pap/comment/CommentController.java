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

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/courses/{courseId}/reviews/{username}/comments")
    public RepresentationModel<CommentDTO> getCommentsForReview(@PathVariable Long courseId, @PathVariable String username) {
        var comments = commentService.getCommentsForReview(courseId, username);

        var commentModelList = comments.stream()
                .map(this::addLinks)
                .toList();

        return HalModelBuilder.emptyHalModel()
                .embed(commentModelList.isEmpty() ? Collections.emptyList() : commentModelList, LinkRelation.of("comments"))
                .link(linkTo(methodOn(CommentController.class).getCommentsForReview(courseId, username)).withSelfRel())
                .build();
    }

    @GetMapping("/api/comments/{commentId}")
    public CommentDTO getCommentById(@PathVariable Long commentId) {
        return commentService.findCommentById(commentId)
                .map(this::addLinks)
                .orElseThrow(() -> new CommentNotFoundException("no comment with ID: " + commentId));
    }

    @GetMapping("/api/users/{username}/comments")
    public RepresentationModel<CommentDTO> getUserComments(@PathVariable String username) {
        var comments = commentService.getCommentsByUsername(username);
        var commentModelList = comments.stream()
                .map(this::addLinks)
                .toList();

        return HalModelBuilder.emptyHalModel()
                .embed(commentModelList.isEmpty() ? Collections.emptyList() : commentModelList, LinkRelation.of("comments"))
                .link(linkTo(methodOn(UserController.class).getUser(username)).withRel("user"))
                .build();
    }

    @PostMapping("/api/courses/{courseId}/reviews/{reviewerUsername}/comments")
    public CommentDTO addComment(
            @PathVariable Long courseId,
            @PathVariable String reviewerUsername,
            @RequestBody AddCommentRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return addLinks(commentService.addNewComment(courseId, reviewerUsername, request, principal));
    }

    @PutMapping("/api/comments/{commentId}")
    public CommentDTO updateComment(@PathVariable Long commentId, @RequestBody UpdateCommentRequest request, @AuthenticationPrincipal UserPrincipal principal) {
        return addLinks(commentService.updateComment(commentId, request, principal));
    }


    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<CommentDTO> deleteComment(@PathVariable Long commentId, @AuthenticationPrincipal UserPrincipal principal) {
        commentService.deleteComment(commentId, principal);
        return ResponseEntity.noContent().build();
    }

    // TODO change this to return 400 instead of 404 when appropriate
    @ExceptionHandler({CommentNotFoundException.class, UserNotFoundException.class, ReviewNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Exception> handleUnauthorized(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Exception> handleForbidden(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
    }

    private CommentDTO addLinks(CommentDTO comment) {
        return comment.add(
                linkTo(methodOn(CommentController.class).getCommentById(comment.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getReview(comment.getCourseId(), comment.getAuthorUsername())).withRel("review"),
                linkTo(methodOn(UserController.class).getUser(comment.getAuthorUsername())).withRel("user")
        );
    }
}