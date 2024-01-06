package pl.edu.pw.pap.review;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.comment.ForbiddenException;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.course.CourseNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.teacher.TeacherNotFoundException;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ReviewService {
    private final Logger log = LoggerFactory.getLogger(ReviewService.class);
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    public final TeacherRepository teacherRepository;


    public ReviewDTO convertToDTO(Review review) {
        return ReviewDTO.builder()
                .authorUsername(review.getUser().getUsername())
                .opinion(review.getOpinion())
                .easeRating(review.getEaseRating())
                .interestRating(review.getInterestRating())
                .interactiveRating(review.getInteractiveRating())
                .created(review.getCreated())
                .courseId(review.getCourse().getId())
                .edited(review.getEdited())
                .build();
    }

    public Optional<ReviewDTO> getReviewById(Long userId, Long courseId) {
        ReviewKey reviewKey = new ReviewKey(userId, courseId);
        return reviewRepository
                .findById(reviewKey)
                .map(this::convertToDTO);
    }

    public List<ReviewDTO> getCourseReviews(Long courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new CourseNotFoundException("No course with id " + courseId));

        return course.getReviews()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }

    public void deleteReview(Long courseId, String username, UserPrincipal userPrincipal) {

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
        if (!user.getId().equals(userPrincipal.getUserId())
                && !userPrincipal.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            // not author and not admin, results in 403 forbidden
            throw (new ForbiddenException("You are not permitted to delete this review"));
        }

        Optional<Review> maybeReview = reviewRepository.findById(new ReviewKey(user.getId(), courseId));
        if (maybeReview.isEmpty()) {
            return;
        }

        log.debug("Trying to remove review");
        Review review = maybeReview.get();

        reviewRepository.delete(review);
    }

    public ReviewDTO addReview(Long courseId, AddReviewRequest request, UserPrincipal userPrincipal) {
        // TODO Change thrown exception to BAD_REQUEST and handle it
        var addingUser = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User with username: " + userPrincipal.getUsername() + "doesn't exist"));
        var course = courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException("No course with id: " + userPrincipal.getUsername()));

        var duplicate = reviewRepository.findByCourse_IdAndUser_Username(courseId, userPrincipal.getUsername());
        if (duplicate.isPresent()) {
            throw (new DuplicateReviewException("Cannot add more than one review to a course"));
        }
        return convertToDTO(
                reviewRepository.save(new Review(addingUser, course, request.text(), request.easeRating(), request.interestingRating(), request.interactiveRating()))
        );
    }

    public List<ReviewDTO> getTeacherReviews(Long teacherId) {
        var teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("No teacher with id: " + teacherId));

        return teacher.getCourses().stream()
                .map(Course::getReviews)
                .flatMap(Collection::stream)
                .map(this::convertToDTO)
                .toList();
    }

    public ReviewDTO editReview(Long courseId, String username, EditReviewRequest request, UserPrincipal userPrincipal) {
        User reviewerUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user (reviewer) with username: " + username));

        // this is better to throw before the author check to not imply that a review exists if just the users dont match
        Review review = reviewRepository.findById(new ReviewKey(reviewerUser.getId(), courseId))
                .orElseThrow(() -> new ReviewNotFoundException("No review of course" + courseId + " by " + username));

        if (!reviewerUser.getId().equals(userPrincipal.getUserId())) {
            // review exists but caller is not author, results in 403 forbidden
            throw (new ForbiddenException("You are not permitted to edit this review"));
        }

        review.setOpinion(request.text());
        review.setOverallRating(request.rating());
        review.setEdited(true);
        return convertToDTO(reviewRepository.save(review));

    }
}
