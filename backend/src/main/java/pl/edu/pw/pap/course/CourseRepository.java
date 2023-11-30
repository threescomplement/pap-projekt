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
            "(:language IS NULL OR c.language = :language) AND " +
            "(:module IS NULL OR (c.module IS NULL AND :module IS NULL) OR c.module = :module) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:level IS NULL OR c.level = :level)")
    List<Course> findCoursesByAttributes(
            @Param("name") String name,
            @Param("language") String language,
            @Param("module") String module,
            @Param("type") String type,
            @Param("level") String level
    );
}
