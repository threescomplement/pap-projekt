package pl.edu.pw.pap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.teacher.Teacher;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;

import java.util.List;

@SpringBootApplication
public class PapApplication {

    private static final Logger log = LoggerFactory.getLogger(PapApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PapApplication.class, args);
    }

    @Bean
    @Profile({"dev", "dev-postgres"})
    public CommandLineRunner addDummyData(
            CourseRepository courseRepository,
            TeacherRepository teacherRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            CommentRepository commentRepository,
            PasswordEncoder passwordEncoder
    ) {
        return (args) -> {
            var user_1 = userRepository.save(new User("rdeckard", "rdeckard@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
            var user_2 = userRepository.save(new User("rbatty", "rbatty@example.com", passwordEncoder.encode("password"), "ROLE_USER", true));
            var admin_1 = userRepository.save(new User("admin", "admin@example.com", passwordEncoder.encode("password"), "ROLE_ADMIN", true));

            var teacher_1 = teacherRepository.save(new Teacher("mgr. Jan Kowalski"));
            var teacher_2 = teacherRepository.save(new Teacher("mgr. Ann Nowak"));


            var course_1 = new Course("Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null);
            var course_2 = new Course("Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15");
            teacher_1.addCourse(course_1);
            teacher_1.addCourse(course_2);
            teacher_1 = teacherRepository.save(teacher_1);

            var course_3 = new Course("Język niemiecki, poziom A2", "Niemiecki", "Akademicki", "A2", "M6");
            var course_4 = new Course("Język włoski dla początkujących", "Włoski", "Akademicki", "A1", "M1");
            teacher_2.addCourse(course_3);
            teacher_2.addCourse(course_4);
            teacher_2 = teacherRepository.save(teacher_2);

            var review = new Review(user_1, course_1, "text", 5);
            reviewRepository.save(review);

            var reviews = reviewRepository.findAll();
            log.info(reviews.toString());


            var teachers = teacherRepository.findAll();
            log.info("Added teachers:");
            teachers.forEach(t -> log.info(t.toString()));

            var courses = courseRepository.findAll();
            log.info("Added courses:");
            courses.forEach(c -> log.info(c.toString()));

            var users = userRepository.findAll();
            log.info("Added users:");
            users.forEach(u -> log.info(u.toString()));

//            var reviews = reviewRepository.findAll();
//            log.info("Added reviews:");
//            reviews.forEach(r -> log.info(r.toString()));
//
//            var comments = commentRepository.findAll();
//            log.info("Added comments:");
//            comments.forEach(c -> log.info(c.toString()));
        };
    }
}
