package pl.edu.pw.pap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;

@SpringBootApplication
public class PapApplication {

    private static final Logger log = LoggerFactory.getLogger(PapApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PapApplication.class, args);
    }

    @Bean()
    @Profile("dev")
    public CommandLineRunner addDummyCourses(CourseRepository repository) {
        return (args) -> {
            repository.save(new Course("Angielski B1"));
            repository.save(new Course("Francuski A2"));
            repository.save(new Course("Niemiecki B1"));
            repository.save(new Course("Angielski C2"));
            log.info("Saved 4 courses to database");

            repository.findAll().forEach(c -> log.info(c.toString()));

            var course = repository.findById(3L).get();
            log.info("Course with id 3: " + course);

            log.info("All english courses:");
            repository.findCoursesByNameContaining("Angielski").forEach(c -> {
                log.info(c.toString());
            });
        };
    }

    @Bean()
    @Profile("dev")
    public CommandLineRunner addDummyUsers(UserRepository repository) {
        return (args) -> {
            repository.save(new User("rdeckard", "rdeckard@example.com", "$2a$12$vyx87ILAKlC2hkoh80nbMe0iXubtm/vgclOS22/Mj8BqToMyPDhb2", "ROLE_ADMIN", true)); // password
            repository.save(new User("rbatty", "rbatty@example.com", "$2a$12$ytByi2pSlciOCNJHAf81K.p1YIqZYx7ATiBl/E.4EVlkBqD8k7Uu.", "ROLE_USER", true)); // password2
        };
    }
}
