package pl.edu.pw.pap.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.course.courseNotFoundException;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;

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

    public void deleteReview ( Long courseId, String username){

        System.out.println("Asked for deletion of review by " + username + " of course " + courseId);
        Optional<User> maybeUser = userRepository.findByUsername(username);
        if (maybeUser.isPresent()){
            System.out.println("Found user of review being deleted");
            User user = maybeUser.get();
            Optional<Review> maybeReview = reviewRepository.findById(new ReviewKey( maybeUser.get().getId(), courseId));
            if (maybeReview.isPresent()){
//                synchronize the entities. Remove comments from Review to allow deletion of comments
                System.out.println("Trying to remove review");
                Review review = maybeReview.get();
//                var comments = review.getComments();
//                commentRepository.deleteAll(comments);
//                remove review from user to allow deletion of review
                // safely delete comments

                reviewRepository.delete(review);
            }
        }

    }
}
