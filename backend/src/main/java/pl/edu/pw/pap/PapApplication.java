package pl.edu.pw.pap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.config.AppConfiguration;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.security.JwtProperties;
import pl.edu.pw.pap.teacher.TeacherRepository;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.utils.DummyData;

@SpringBootApplication
public class PapApplication {

    private static final Logger log = LoggerFactory.getLogger(PapApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PapApplication.class, args);
    }

    @Bean
    public CommandLineRunner reportStatus(
            JwtProperties jwtProperties,
            AppConfiguration appConfiguration
    ) {
        return (args) -> {
            if (jwtProperties.getSecretKey() != null) {
                log.info("Loaded secret key for signing JWT tokens");
            } else {
                log.error("Missing JWT signing secret key");
            }

            log.info(String.format("Schedule for deleting expired tokens (cron): %s", appConfiguration.getDeleteTokensCronExpression()));
        };
    }

    //    @Bean
    @Profile({"dev", "dev-postgres"})
    public CommandLineRunner addDummyData(
            CourseRepository courseRepository,
            TeacherRepository teacherRepository,
            UserRepository userRepository,
            ReviewRepository reviewRepository,
            CommentRepository commentRepository,
            DummyData generator
    ) {
        return (args) -> {
            generator.addDummyData();

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
