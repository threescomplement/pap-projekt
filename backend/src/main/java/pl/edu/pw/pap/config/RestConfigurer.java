package pl.edu.pw.pap.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.teacher.Teacher;

@Configuration
public class RestConfigurer implements RepositoryRestConfigurer {
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        config.exposeIdsFor(Course.class);
        config.exposeIdsFor(Teacher.class);
        config.disableDefaultExposure();
    }
}
