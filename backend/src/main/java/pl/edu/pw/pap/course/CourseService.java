package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.teacher.TeacherNotFoundException;
import pl.edu.pw.pap.teacher.TeacherRepository;

import java.util.List;
import java.util.Optional;

import static pl.edu.pw.pap.common.Constants.ALL;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final TeacherRepository teacherRepository;


    public CourseDTO convertToDto(Course course){
        // An additional call to the database is bad but otherwise I'd have to make an averageRating function which
        // Would have already been done earlier I imagine
        // TODO: Consider getting the average rating of a course by just counting the ratings from course.getRatings()

        return courseRepository
                .findByIdWithRating(course.getId())
                .orElseThrow( () -> new CourseNotFoundException("Conversion to CourseDTO failed"));
    }

    public Optional<CourseDTO> getById(Long courseId) {
        return courseRepository.findByIdWithRating(courseId);
    }

    public List<CourseDTO> getTeacherCourses(Long teacherId){
        var teacher = teacherRepository
                    .findById(teacherId)
                    .orElseThrow( () -> new TeacherNotFoundException("No teacher with given id: " + teacherId));
        return teacher
                .getCourses()
                .stream()
                .map(this::convertToDto)
                .toList();

    }

    public List<CourseDTO> getAllMatchingFilters(String name, String language, String module, String type, String level, String teacherName) {
        return courseRepository.findCoursesByAttributesWithRatings(
                name,
                language.equals(ALL) ? null : language,
                module.equals(ALL) ? null : module,
                type.equals(ALL) ? null : type,
                level.equals(ALL) ? null : level,
                teacherName
        );
    }
}
