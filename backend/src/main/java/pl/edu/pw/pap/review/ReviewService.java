package pl.edu.pw.pap.review;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.course.courseNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;


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
}
