package pl.edu.pw.pap.misc;

import org.hibernate.Hibernate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.pap.PapApplication;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.report.ReviewReportRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PapApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ModelIntegrityConstraintsTest {
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
    @Autowired
    DummyData data;
    @Autowired
    ReviewReportRepository reviewReportRepository;
    @Autowired
    CommentReportRepository commentReportRepository;

    @Test
    void testRelatedComments() {
        var teacher = teacherRepository.save(new Teacher("teacher"));
        var course = courseRepository.save(new Course("course name", "language", "type", "level", null, teacher));
        var user_1 = userRepository.save(new User("user_1", "user@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
        var user_2 = userRepository.save(new User("user_2", "user2@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));

        var review = reviewRepository.save(new Review(user_1, course, "Opinion", 8, 9, 10));
        var comment = new Comment("comment", review, user_2);
        review = reviewRepository.save(review);  // Does not work if review is not saved TODO make it work if only comment gets saved
        commentRepository.save(comment);

        review = reviewRepository.findById(review.getId()).get();  // refresh
        assertEquals(1, review.getComments().size());
        assertEquals(1, commentRepository.findAll().size());

        reviewRepository.delete(review);
        assertEquals(0, commentRepository.findAll().size());
    }

    @Test
    void testOnDummyData() {
        data.addDummyData();

        var review = reviewRepository.findById(data.review_1.getId()).get();
        assertEquals(1, review.getComments().size());
        data.deleteAll();
    }

//
//    @Test
//    void testReviewReportCascade(){
//        data.addDummyData();
//        var review1 = reviewRepository.findById(data.review_1.getId()).get();
//        var review2 = reviewRepository.findById(data.review_2.getId()).get();
//        var review4 = reviewRepository.findById(data.review_4.getId()).get();
//
//        var reviewReports = reviewReportRepository.findAll();
//        assertEquals(3, reviewReports.size());
//
//        reviewRepository.delete(review1);
//        reviewReports = reviewReportRepository.findAll();
//        assertEquals(3, reviewReports.size());
//
//        reviewRepository.delete(review2);
//        reviewReports = reviewReportRepository.findAll();
//        assertEquals(1, reviewReports.size());
//
//        reviewRepository.delete(review4);
//        reviewReports = reviewReportRepository.findAll();
//        assertEquals(0, reviewReports.size());
//
//        data.deleteAll();
//    }
//
//    @Test
//    void testDeleteReviewReport(){
//        data.addDummyData();
//        var review2Report = reviewReportRepository.findById(1L).get();
//        System.out.println(review2Report.toString());
//        reviewReportRepository.delete(review2Report);
//
//        var reviewReports = reviewReportRepository.findAll();
//        assertEquals(2, reviewReports.size());
//
//        data.deleteAll();
//    }
//
//
//    @Test
//    void testDeleteComment(){
//        data.addDummyData();
//        var comment = commentRepository.findById(1L).get();
//        commentRepository.delete(comment);
//        var comments = commentRepository.findAll();
//        assertEquals(4, comments.size());
//
//        data.deleteAll();
//    }
//
//
//
//
//
//    @Test
//    void testAccessReviewFromReport(){
//        data.addDummyData();
//
//        var review2Report = reviewReportRepository.findById(1L).get();
//        var review2 = review2Report.getReported();
//        assertEquals(data.review_2.getId(), review2.getId());
//        assertEquals(data.review_2.getOpinion(), review2.getOpinion());
//
//        data.deleteAll();
//    }
//
//

}
