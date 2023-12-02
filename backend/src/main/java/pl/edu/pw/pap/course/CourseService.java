package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static pl.edu.pw.pap.common.Constants.ALL;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public Optional<CourseDTO> getById(Long courseId) {
        return courseRepository.findByIdWithRating(courseId);
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
