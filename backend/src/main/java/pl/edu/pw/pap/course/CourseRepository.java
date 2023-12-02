package pl.edu.pw.pap.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE " +
            "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:language IS NULL OR LOWER(c.language) = LOWER(:language)) AND " +
            "(:module IS NULL OR (c.module IS NULL AND :module IS NULL) OR c.module = :module) AND " +
            "(:type IS NULL OR c.type = :type) AND " +
            "(:level IS NULL OR c.level = :level) AND " +
            "(:teacherName is NULL OR LOWER(c.teacher.name) LIKE LOWER(CONCAT('%', :teacherName, '%')))"
    )
    List<Course> findCoursesByAttributes(
            @Param("name") String name,
            @Param("language") String language,
            @Param("module") String module,
            @Param("type") String type,
            @Param("level") String level,
            @Param("teacherName") String teacherName
    );

    @Query("SELECT new pl.edu.pw.pap.course.CourseDTO(c.id, c.name, c.language, c.type, c.level, c.module, AVG(r.overallRating))" +
            "FROM Course c LEFT JOIN Review r ON c.id = r.course.id " +
            "GROUP BY c.id, c.name, c.language, c.level, c.module " +
            "HAVING c.id = :id "
    )
    Optional<CourseDTO> findByIdWithRating(@Param("id") Long id);


    @Query("SELECT new pl.edu.pw.pap.course.CourseDTO(c.id, c.name, c.language, c.type, c.level, c.module, AVG(r.overallRating))" +
            "FROM Course c LEFT JOIN Review r ON c.id = r.course.id " +
            "GROUP BY c.id, c.name, c.language, c.level, c.module"
    )
    List<CourseDTO> findAllWithRatings();

}
