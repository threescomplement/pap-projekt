package pl.edu.pw.pap.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:language IS NULL OR LOWER(c.language) LIKE LOWER(CONCAT('%', :language, '%'))) AND " +
            "(:module IS NULL OR LOWER(c.module) LIKE LOWER(CONCAT('%', :module, '%'))) AND " +
            "(:type IS NULL OR LOWER(c.type) LIKE LOWER(CONCAT('%', :type, '%'))) AND " +
            "(:level IS NULL OR LOWER(c.level) LIKE LOWER(CONCAT('%', :level, '%')))")
    List<Course> findCoursesByAttributes(
            @Param("name") String name,
            @Param("language") String language,
            @Param("module") String module,
            @Param("type") String type,
            @Param("level") String level
    );
}
