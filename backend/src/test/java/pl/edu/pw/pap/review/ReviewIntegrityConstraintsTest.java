package pl.edu.pw.pap.review;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.pap.PapApplication;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReviewIntegrityConstraintsTest {
    @LocalServerPort
    private int port;

    @Autowired
    ReviewRepository reviewRepository;
    @Autowired
    CommentRepository commentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;


//    @Test
//    void testRelatedComments() {
//        var teacher = teacherRepository.save(new Teacher("teacher"));
//        var course = courseRepository.save(new Course("course name", "language", "type", "level", null, teacher));
//        var user_1 = userRepository.save(new User("user_1", "user@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
//        var user_2 = userRepository.save(new User("user_2", "user2@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
//
//        var review = new Review(user_1, course, "Opinion", 8);
//        var comment = new Comment("comment", user_2);
//
//        user_2.addComment(comment);
//        review.addComment(comment);
//        review = reviewRepository.save(review);
//        user_2 = userRepository.save(user_2);
//
//        assertEquals("Opinion", comment.getReview().getOpinion());
//        assertEquals(1, review.getComments().size());
//        assertEquals(1, user_2.getComments().size());
//        assertEquals(1, commentRepository.findAll().size());
//
//
//        reviewRepository.delete(review);
//        assertEquals(0, reviewRepository.findAll().size());
//        assertEquals(0, commentRepository.findAll().size());
//        user_2 = userRepository.findById(user_2.getId()).get();
//        assertEquals(0, user_2.getComments().size());
//    }
}
