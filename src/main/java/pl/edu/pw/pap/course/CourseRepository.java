package pl.edu.pw.pap.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    //todo: resolve lower/uppercase difference when fetching by name
    List<Course> findCoursesByNameContaining(String name);
    List<Course> findCoursesByLanguageContaining(String language);
    List<Course> findCoursesByTypeContaining(String type);
    List<Course> findCoursesByLevelContaining(String level);
}
