package pl.edu.pw.pap.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.comment.UnauthorizedException;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.course.courseNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.userNotFoundException;

import javax.sound.sampled.ReverbType;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public Optional<Review> getReviewById(Long userId, Long courseId ){
        ReviewKey reviewKey = new ReviewKey(userId, courseId);
        return reviewRepository.findById(reviewKey);
    }

    public List<Review> getCourseReviews(Long courseId) {
        Optional<Course> getCourse = courseRepository.findById(courseId);
        if (getCourse.isEmpty()){
            throw new courseNotFoundException("No course with id " + courseId);
        }
        Course course = getCourse.get();
        return new ArrayList<Review>(course.getReviews());
    }

    public void deleteReview (Long courseId, String username, UserPrincipal userPrincipal){

        System.out.println("Asked for deletion of review by " + username + " of course " + courseId);
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isPresent()){
            System.out.println("Found user of review being deleted");
            User user = maybeUser.get();
            if (!user.getId().equals(userPrincipal.getUserId())){
                throw new UnauthorizedException("Only the owner of the review can delete it.");
            }
            Optional<Review> maybeReview = reviewRepository.findById(new ReviewKey( maybeUser.get().getId(), courseId));
            if (maybeReview.isPresent()){
                System.out.println("Trying to remove review");
                Review review = maybeReview.get();

                reviewRepository.delete(review);
            }
        }

    }

    public Review addReview(AddReviewRequest request, UserPrincipal userPrincipal) {

        Optional<User> maybeUser = userRepository.findByUsername(request.username());
        if (maybeUser.isEmpty()){
            throw new userNotFoundException("No user with given username: " + request.username());
        }
        System.out.println("Found user of review being deleted");
        Optional<Course> maybeCourse = courseRepository.findById(request.courseId());
        if (maybeCourse.isEmpty()){
            throw new courseNotFoundException("No course with id: " + request.courseId());
        }
        User user = maybeUser.get();
        if (!user.getId().equals(userPrincipal.getUserId())){
            throw new UnauthorizedException("User can only add reviews in his own name.");
        }
        Course course = maybeCourse.get();
        return reviewRepository.save(new Review(user, course, request.text(), request.rating()));
    }

}
