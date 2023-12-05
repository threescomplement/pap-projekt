package pl.edu.pw.pap.review;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.UnauthorizedException;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.course.CourseNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public Optional<Review> getReviewById(Long userId, Long courseId ){
        ReviewKey reviewKey = new ReviewKey(userId, courseId);
        return reviewRepository.findById(reviewKey);
    }

    public List<Review> getCourseReviews(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new CourseNotFoundException("No course with id " + courseId));

        return course.getReviews()
                .stream()
                .toList();
    }

    public void deleteReview (Long courseId, String username, UserPrincipal userPrincipal){

        log.debug("Asked for deletion of review by " + username + " of course " + courseId);
        // Not sure if this can be simplified any more than this
        // It has to check if the parameters exist before it takes the review (I think?)
        // I could throw exceptions and ignore them but that sounds like bad practise
        // might try with nulls everywhere after tests are done
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isEmpty()) {
            return;
        }

        log.debug("Found user of review being deleted");
        User user = maybeUser.get();
        if (!user.getId().equals(userPrincipal.getUserId())) {
            return;  // TODO its not ok, should be Forbidden 403 unless admin
        }

        Optional<Review> maybeReview = reviewRepository.findById(new ReviewKey(user.getId(), courseId));
        if (maybeReview.isEmpty()){
            return;
        }

        log.debug("Trying to remove review");
        Review review = maybeReview.get();

        reviewRepository.delete(review);
    }

    public Review addReview(AddReviewRequest request, UserPrincipal userPrincipal) {
        var user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UserNotFoundException("No user with given username: " + request.username()));

        var course = courseRepository.findById(request.courseId())
                .orElseThrow(() -> new CourseNotFoundException("No course with id: " + request.courseId()));

        // TODO: Use only the userPrincipal to determine the user, skipping this check
        if (!user.getId().equals(userPrincipal.getUserId())){
            throw new UnauthorizedException("User can only add reviews in his own name.");
        }

        return reviewRepository.save(new Review(user, course, request.text(), request.rating()));
    }

}
