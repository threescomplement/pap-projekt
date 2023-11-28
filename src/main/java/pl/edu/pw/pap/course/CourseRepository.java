package pl.edu.pw.pap.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findCoursesByNameContaining(String name);
    List<Course> findCoursesByLanguage(String language);
    List<Course> findCoursesByType(String type);
    List<Course> findCoursesByLevel(String level);
}
