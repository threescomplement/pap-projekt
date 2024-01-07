package pl.edu.pw.pap.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.course.Course;
import pl.edu.pw.pap.review.Review;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static pl.edu.pw.pap.common.Constants.ALL;

@Service
@RequiredArgsConstructor
public class TeacherService {
    private final TeacherRepository teacherRepository;


    public Optional<TeacherDTO> getTeacherById(Long id) {
        return teacherRepository.findById(id)
                .map(this::convertToDto);
    }

    public List<TeacherDTO> getTeachersMatchingFilters(String name, String language) {
        return teacherRepository
                .findTeachersMatchingFilters(name, language.equals(ALL) ? null : language)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public TeacherDTO convertToDto(Teacher teacher) {
        var reviews = getTeacherReviews(teacher);
        return TeacherDTO.builder()
                .id(teacher.getId())
                .name(teacher.getName())
                .averageRating(reviews.stream()
                        .mapToDouble(Review::getOverallRating)
                        .average()
                        .orElse(0)
                )
                .numberOfRatings(reviews.size())
                .build();
    }

    public List<Review> getTeacherReviews(Teacher teacher) {
        return teacher.getCourses().stream()
                .map(Course::getReviews)
                .flatMap(Collection::stream)
                .toList();
    }
}