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


    public CourseDTO convertToDto(Course course) {
        var reviews = course.getReviews();
        double averageEaseRating = 0;
        double averageInterestRating = 0;
        double averageEngagementRating = 0;
        int easeRatingSum = 0;
        int interestRatingSum = 0;
        int engagementRatingSum = 0;
        for (var review : reviews) {
            easeRatingSum += review.getEaseRating();
            interestRatingSum += review.getInterestRating();
            engagementRatingSum += review.getEngagementRating();
        }
        if (!reviews.isEmpty()) {
            averageEaseRating = (double) easeRatingSum / (reviews.size());
            averageInterestRating = (double) interestRatingSum / (reviews.size());
            averageEngagementRating= (double) engagementRatingSum / (reviews.size());
        }


        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .language(course.getLanguage())
                .type(course.getType())
                .level(course.getLevel())
                .module(course.getModule())
                .averageEaseRating(averageEaseRating)
                .averageInterestRating(averageInterestRating)
                .averageEngagementRating(averageEngagementRating)
                .teacherId(course.getTeacher().getId())
                .build();
    }

    public Optional<CourseDTO> getById(Long courseId) {
        return courseRepository.findByIdWithRating(courseId);
    }

    public List<CourseDTO> getTeacherCourses(Long teacherId) {
        var teacher = teacherRepository
                .findById(teacherId)
                .orElseThrow(() -> new TeacherNotFoundException("No teacher with given id: " + teacherId));
        return teacher
                .getCourses()
                .stream()
                .map(this::convertToDto)
                .toList();

    }

    // TODO write a repository method that works with both H2 and postgres
    public List<CourseDTO> getAllMatchingFilters(String name, String language, String module, String type, String level, String teacherName) {
        return courseRepository.findAll().stream()
                .filter(c -> c.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(c -> language.equals(ALL) || c.getLanguage().equalsIgnoreCase(language))
                .filter(c -> module.equals(ALL) || (c.getModule() != null && c.getModule().equalsIgnoreCase(module)))
                .filter(c -> type.equals(ALL) || c.getType().equalsIgnoreCase(type))
                .filter(c -> level.equals(ALL) || c.getLevel().equalsIgnoreCase(level))
                .filter(c -> c.getTeacher().getName().toLowerCase().contains(teacherName.toLowerCase()))
                .map(this::convertToDto)
                .toList();
    }
}
