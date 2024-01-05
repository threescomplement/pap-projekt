package pl.edu.pw.pap.utils;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.emailverification.EmailVerificationTokenRepository;

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

        review_1 = reviewRepository.save(new Review(user_1, course_1, "Dobrze prowadzony kurs, wymagający nauczyciel", 8));
        review_2 = reviewRepository.save(new Review(user_2, course_1, "Zbyt duże wymagania do studentów", 3));
        review_3 = reviewRepository.save(new Review(user_2, course_4, "Świetne wprowadzenie do języka", 10));
        review_4 = reviewRepository.save(new Review(user_1, course_3, "W porządku", 6));

        comment_1 = commentRepository.save(new Comment("rel", review_1, user_3));
        comment_2 = commentRepository.save(new Comment("czyli co?", review_4, user_2));
        comment_3 = commentRepository.save(new Comment("przesada", review_2, user_1));
        comment_4 = commentRepository.save(new Comment("trudne serio", review_2, user_2));
        comment_5 = commentRepository.save(new Comment("oj tak", review_2, user_3));

        reviewRepository.saveAll(List.of(review_1, review_2, review_3, review_4));  // Will not work otherwise FIXME
    }

    public void deleteAll() {
        emailVerificationTokenRepository.deleteAll();
        userRepository.deleteAll();
        teacherRepository.deleteAll();
        courseRepository.deleteAll();
        reviewRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
