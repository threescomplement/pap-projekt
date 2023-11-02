package pl.edu.pw.pap.course;

import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends CrudRepository<Course, Long> {
    List<Course> findCoursesByNameContaining(String name);
}
