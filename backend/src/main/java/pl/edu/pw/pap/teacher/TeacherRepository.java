package pl.edu.pw.pap.teacher;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {

    @Query("SELECT new pl.edu.pw.pap.teacher.TeacherDTO(t.id, t.name, AVG(r.easeRating), AVG(r.interestRating), AVG(r.interactiveRating))" +
            "FROM Teacher t LEFT JOIN Review r ON t.id = r.course.teacher.id " +
            "WHERE (:name IS NULL OR LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%')))" +
            "AND (:language IS NULL OR r.course.language = :language)" +
            "GROUP BY t.id, t.name"
    )
    List<TeacherDTO> findTeachersMatchingFilters(
            @Param("name") String name,
            @Param("language") String language
    );

    // TODO: querying for number of reviews
    @Query("SELECT new pl.edu.pw.pap.teacher.TeacherDTO(t.id, t.name, AVG(r.easeRating), AVG(r.interestRating), AVG(r.interactiveRating))" +
            "FROM Teacher t LEFT JOIN Review r ON t.id = r.course.teacher.id " +
            "WHERE t.id = :id " +
            "GROUP BY t.id, t.name"
    )
    Optional<TeacherDTO> findByIdWithRating(@Param("id") Long id);
}
