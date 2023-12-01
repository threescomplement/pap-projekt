package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;

    public Optional<Course> getById(Long courseId) {
        return courseRepository.findById(courseId);
    }

    public List<Course> getAllMatchingFilters(String name, String language, String module, String type, String level) {
        return courseRepository.findCoursesByAttributes(name, language, module, type, level);
    }
}
