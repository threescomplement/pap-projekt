package pl.edu.pw.pap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.course.CourseRepository;

@SpringBootApplication
public class PapApplication {

	private static final Logger log = LoggerFactory.getLogger(PapApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(PapApplication.class, args);
		System.out.println("Hello world");
	}

	@Bean
	public CommandLineRunner demo(CourseRepository repository) {
		return (args) -> {
			repository.save(new Course("Angielski B1"));
			repository.save(new Course("Francuski A2"));
			repository.save(new Course("Niemiecki B1"));
			repository.save(new Course("Angielski C2"));
			log.info("Saved 4 courses to database");

			repository.findAll().forEach(c -> log.info(c.toString()));

			var course = repository.findById(3L).get();
			log.info("Course with id 3: " + course.toString());

			log.info("All english courses:");
			repository.findCoursesByNameContaining("Angielski").forEach(c -> {
				log.info(c.toString());
			});
		};
	}

}
