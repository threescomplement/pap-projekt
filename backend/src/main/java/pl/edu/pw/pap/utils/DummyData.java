package pl.edu.pw.pap.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.report.ReportStatus;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.report.ReviewReport;
import pl.edu.pw.pap.review.report.ReviewReportRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Utility for creating and deleting data, useful for testing
 */
@Service
@RequiredArgsConstructor
public class DummyData {
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final EmailVerificationTokenRepository emailVerificationTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewReportRepository reviewReportRepository;
    private final CommentReportRepository commentReportRepository;

    public User user_1;
    public User user_2;
    public User user_3;
    public User admin_1;
    public Teacher teacher_1;
    public Teacher teacher_2;
    public Teacher teacher_3;
    public Course course_1;
    public Course course_2;
    public Course course_3;
    public Course course_4;
    public Review review_1;
    public Review review_2;
    public Review review_3;
    public Review review_4;
    public Comment comment_1;
    public Comment comment_2;
    public Comment comment_3;
    public Comment comment_4;
    public Comment comment_5;
    public ReviewReport reviewReport_1;
    public ReviewReport reviewReport_2;
    public ReviewReport reviewReport_3;
    public CommentReport commentReport_1;
    public CommentReport commentReport_2;
    public CommentReport commentReport_3;

    public void addDummyData() {
        user_1 = userRepository.save(new User("rdeckard", "rdeckard@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
        user_2 = userRepository.save(new User("rbatty", "rbatty@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
        user_3 = userRepository.save(new User("user_3", "user3@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true));
        admin_1 = userRepository.save(new User("admin", "admin@example.com", passwordEncoder.encode("password"), "ROLE_ADMIN", true));

        teacher_1 = teacherRepository.save(new Teacher("mgr. Jan Kowalski"));
        teacher_2 = teacherRepository.save(new Teacher("mgr. Ann Nowak"));
        teacher_3 = teacherRepository.save(new Teacher("mgr. Andrzej Sysy"));

        course_1 = courseRepository.save(new Course("Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, teacher_1));
        course_2 = courseRepository.save(new Course("Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15", teacher_1));
        course_3 = courseRepository.save(new Course("Język niemiecki, poziom A2", "Niemiecki", "Akademicki", "A2", "M6", teacher_2));
        course_4 = courseRepository.save(new Course("Język włoski dla początkujących", "Włoski", "Akademicki", "A1", "M1", teacher_2));

        review_1 = reviewRepository.save(new Review(user_1, course_1, "Dobrze prowadzony kurs, wymagający nauczyciel", 8, 7, 6));
        review_2 = reviewRepository.save(new Review(user_2, course_1, "Zbyt duże wymagania do studentów", 3, 4, 5));
        review_3 = reviewRepository.save(new Review(user_2, course_4, "Świetne wprowadzenie do języka", 10, 10, 9));
        review_4 = reviewRepository.save(new Review(user_1, course_3, "W porządku", 6, 7, 8));

        comment_1 = commentRepository.save(new Comment("rel", review_1, user_3));
        comment_2 = commentRepository.save(new Comment("czyli co?", review_4, user_2));
        comment_3 = commentRepository.save(new Comment("przesada", review_2, user_1));
        comment_4 = commentRepository.save(new Comment("trudne serio", review_2, user_2));
        comment_5 = commentRepository.save(new Comment("oj tak", review_2, user_3));

//        rdeckard: 1 review reports, user3: 2 review reports
//         review_2 has 2 reports, review4 has 1 report
        reviewReport_1 = reviewReportRepository.save(new ReviewReport(user_1, "obelgi w strone prowadzacego", review_2));
        reviewReport_2 = new ReviewReport(user_3, "nie obiektywna ocena", review_2);
        reviewReport_2.setResolved(true);
        reviewReport_2.setResolvedTimestamp(Timestamp.from(Instant.now()));
        reviewReport_2.setStatus(ReportStatus.DISCARDED);
        reviewReport_2.setResolvedByUsername("admin");

        reviewReport_2 = reviewReportRepository.save(reviewReport_2);
        reviewReport_3 =  reviewReportRepository.save(new ReviewReport(user_3, "", review_4));

        // rbatty: 1 comment reports, user3: 2 comment reports
        // comment_2 has 2 reports, comment_4 has 1 report
        commentReport_1 = commentReportRepository.save(new CommentReport(user_3, "brak kultury", comment_2));
        commentReport_2 = commentReportRepository.save(new CommentReport(user_3, "skill issue", comment_4));
        commentReport_3 = commentReportRepository.save(new CommentReport(user_2, "jajo", comment_2));



        reviewRepository.saveAll(List.of(review_1, review_2, review_3, review_4));  // Will not work otherwise FIXME
    }

    public void deleteAll() {
        emailVerificationTokenRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        reviewRepository.deleteAll();
        commentRepository.deleteAll();
        reviewReportRepository.deleteAll();
        commentReportRepository.deleteAll();
    }
}
