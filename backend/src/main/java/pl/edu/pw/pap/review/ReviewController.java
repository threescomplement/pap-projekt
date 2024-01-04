package pl.edu.pw.pap.review;


import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.CommentNotFoundException;
import pl.edu.pw.pap.comment.ForbiddenException;
import pl.edu.pw.pap.comment.UnauthorizedException;
import pl.edu.pw.pap.course.CourseController;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserController;
import pl.edu.pw.pap.user.UserNotFoundException;
import pl.edu.pw.pap.user.UserRepository;

import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;


    @GetMapping("/api/courses/{courseId}/reviews/{username}")
    public ReviewDTO getReview(@PathVariable Long courseId, @PathVariable String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user with username " + username));

        return reviewService
                .getReviewById(user.getId(), courseId)
                .map(this::addLinks)
                .orElseThrow(() -> new ReviewNotFoundException("No review of course " + courseId + " by " + username));
    }


    @GetMapping("/api/courses/{courseId}/reviews")
    public RepresentationModel<ReviewDTO> getCourseReviews(@PathVariable Long courseId) {
        var reviews = reviewService.getCourseReviews(courseId);

        var reviewModelList = reviews
                .stream()
                .map(this::addLinks)
                .toList();

        return HalModelBuilder.emptyHalModel()
                .embed(reviewModelList.isEmpty() ? Collections.emptyList() : reviewModelList, LinkRelation.of("reviews"))
                .links(List.of(
                        linkTo(methodOn(ReviewController.class).getCourseReviews(courseId)).withSelfRel(),
                        linkTo(methodOn(CourseController.class).getCourseById(courseId)).withRel("course"))
                )
                .build();
    }


    @GetMapping("/api/reviews/{username}")
    public RepresentationModel<ReviewDTO> getUserReviews(@PathVariable String username) {

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("No user with username " + username));

        var reviewModelList = user
                .getReviews()
                .stream()
                .map(reviewService::convertToDTO)
                .map(this::addLinks)
                .toList();

        Link selfLink = linkTo(methodOn(ReviewController.class).getUserReviews(username)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(username)).withRel("user");
        var links = List.of(selfLink, userLink);

        // TODO: check if this returns a proper list of links
        return HalModelBuilder.emptyHalModel()
                .embed(reviewModelList.isEmpty() ? Collections.emptyList() : reviewModelList, LinkRelation.of("reviews"))
                .links(links)
                .build();
    }


    @PostMapping("/api/courses/{courseId}/reviews")
    public ReviewDTO addReview(@PathVariable Long courseId, @RequestBody AddReviewRequest request, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return reviewService.addReview(courseId, request, userPrincipal);
    }


    @DeleteMapping("/api/courses/{courseId}/reviews/{username}")
    public ResponseEntity<Review> deleteReview(@PathVariable Long courseId, @PathVariable String username, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        reviewService.deleteReview(courseId, username, userPrincipal);
        return ResponseEntity.noContent().build();
    }

    //TODO get all reviews about given teacher


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

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<Exception> handleDuplicateReview(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }

    private ReviewDTO addLinks(ReviewDTO review) {
        return review.add(
                linkTo(methodOn(ReviewController.class).getReview(review.getCourseId(), review.getAuthorUsername())).withSelfRel(),
                linkTo(methodOn(UserController.class).getUser(review.getAuthorUsername())).withRel("user"),
                linkTo(methodOn(CommentController.class).getCommentsForReview(review.getCourseId(), review.getAuthorUsername())).withRel("comments"),
                linkTo(methodOn(CourseController.class).getCourseById(review.getCourseId())).withRel("course")
        );
    }


}


