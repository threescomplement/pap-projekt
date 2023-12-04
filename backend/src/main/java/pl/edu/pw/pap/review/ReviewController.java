package pl.edu.pw.pap.review;


import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.UnauthorizedException;
import pl.edu.pw.pap.comment.CommentNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserController;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final UserRepository userRepository;



    @GetMapping("/api/courses/{courseId}/reviews/{username}")
    public EntityModel<Review> getReview(@PathVariable Long courseId, @PathVariable String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("No user with username " + username) );

        Review review = reviewService.getReviewById(user.getId(), courseId).orElseThrow(
                () -> new ReviewNotFoundException("No review of course " + courseId + " by " + username) );

        Link selfLink = linkTo(methodOn(ReviewController.class).getReview(courseId, username)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(username)).withRel("user");
        Link commentsLink = linkTo(methodOn(CommentController.class).getCommentsForReview(courseId, user.getUsername())).withRel("comments");
        // TODO: Add link from CourseController
//        Link courseLink = linkTo(method)
        Link[] links = {selfLink, userLink, commentsLink};
        return EntityModel.of(review, links);
    }


    @GetMapping("/api/courses/{courseId}/reviews")
    public CollectionModel<EntityModel<Review>> getCourseReviews(@PathVariable Long courseId) {
        var reviews = reviewService.getCourseReviews(courseId);
        List<EntityModel<Review>> reviewModelList = new ArrayList<>();
        for (Review review: reviews) {
            reviewModelList.add(getReview(review.getId().courseId, review.getUser().getUsername()));
        }
        // TODO: Add course link to collection model of reviews of a course
//        Link courseLink = linkTo(methodOn())

        return CollectionModel.of(
                reviewModelList
        );
    }


    @GetMapping("/api/reviews/{username}")
    public CollectionModel<EntityModel<Review>> getUserReviews(@PathVariable String username){

        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("No user with username " + username));

        List<EntityModel<Review>> reviewModelList = new ArrayList<>();
        for (Review review: user.getReviews()) {
            reviewModelList.add(getReview(review.getCourse().getId(), review.getUser().getUsername()));
        }

        Link selfLink = linkTo(methodOn(ReviewController.class).getUserReviews(username)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(username)).withRel("user");
        Link[] linkList = {selfLink, userLink};
        return CollectionModel.of(
                reviewModelList,
                linkList
        );

    }


    @PostMapping("/api/courses/{courseId}/reviews/{username}") // TODO: change request to take username from principal
    public Review addReview(@RequestBody AddReviewRequest request, UserPrincipal userPrincipal){
        return reviewService.addReview(request, userPrincipal);
    }


    @DeleteMapping("/api/courses/{courseId}/reviews/{username}")
    public ResponseEntity<Review> deleteReview(@PathVariable Long courseId, @PathVariable String username, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        reviewService.deleteReview(courseId, username, userPrincipal);
        return ResponseEntity.noContent().build();
    }



    @ExceptionHandler({CommentNotFoundException.class, UserNotFoundException.class, ReviewNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) { return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);}


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Exception> handleUnauthorized(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e);
    }
}


