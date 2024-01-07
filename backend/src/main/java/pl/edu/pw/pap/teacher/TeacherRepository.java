package pl.edu.pw.pap.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("SELECT t FROM Teacher t " +
            "LEFT JOIN Review r ON t.id = r.course.teacher.id " +
            "WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:language IS NULL OR LOWER(r.course.language) = LOWER(:language))" +
            "GROUP BY t.id, t.name"
    )
    List<Teacher> findTeachersMatchingFilters(
            @Param("name") String name,
            @Param("language") String language
    );
}
