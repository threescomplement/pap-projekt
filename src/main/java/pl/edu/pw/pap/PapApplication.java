package pl.edu.pw.pap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
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

import java.util.List;

@SpringBootApplication
public class PapApplication {

    private static final Logger log = LoggerFactory.getLogger(PapApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PapApplication.class, args);
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner addDummyData(
            CourseRepository courseRepository,
            TeacherRepository teacherRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            CommentRepository commentRepository
    ) {
        return (args) -> {
            var user_1 = new User("rdeckard", "rdeckard@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
            var user_2 = new User("rbatty", "rbatty@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_USER", true); // password
            var admin_1 = new User("admin", "admin@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_ADMIN", true); // password

            var teacher_1 = new Teacher("mgr. Jan Kowalski");
            var teacher_2 = new Teacher("mgr. Ann Nowak");

            var course_1 = new Course("Angielski w biznesie", "Angielski", "Biznesowy", "B2+", null, teacher_1);
            var course_2 = new Course("Język angielski poziom C1", "Angielski", "Ogólny", "C1", "M15", teacher_1);
            var course_3 = new Course("Język niemiecki, poziom A2", "Niemiecki", "Akademicki", "A2", "M6", teacher_2);
            var course_4 = new Course("Język włoski dla początkujących", "Włoski", "Akademicki", "A1", "M1", teacher_2);

            var review_1 = new Review(user_1, course_1, "Dobrze prowadzony kurs, wymagający nauczyciel", 8);
            var review_2 = new Review(user_2, course_1, "Zbyt duże wymagania do studentów", 3);
            var review_3 = new Review(user_2, course_4, "Świetne wprowadzenie do języka", 10);
            var review_4 = new Review(user_1, course_3, "W porządku", 6);

            var comment_1 = new Comment("Przesada", review_2, user_1);
            var comment_2 = new Comment("Pełna zgoda", review_3, user_1);

            userRepository.saveAll(List.of(user_1, user_2, admin_1));
            teacherRepository.saveAll(List.of(teacher_1, teacher_2));
            courseRepository.saveAll(List.of(course_1, course_2, course_3, course_4));
            reviewRepository.saveAll(List.of(review_1, review_2, review_3, review_4));
            commentRepository.saveAll(List.of(comment_1, comment_2));

            var teachers = teacherRepository.findAll();
            log.info("Added teachers:");
            teachers.forEach(t -> log.info(t.toString()));

            var courses = courseRepository.findAll();
            log.info("Added courses:");
            courses.forEach(c -> log.info(c.toString()));

            var users = userRepository.findAll();
            log.info("Added users:");
            users.forEach(u -> log.info(u.toString()));

            var reviews = reviewRepository.findAll();
            log.info("Added reviews:");
            reviews.forEach(r -> log.info(r.toString()));

            var comments = commentRepository.findAll();
            log.info("Added comments:");
            comments.forEach(c -> log.info(c.toString()));
        };
    }
}
