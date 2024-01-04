package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.teacher.TeacherController;
import pl.edu.pw.pap.teacher.TeacherNotFoundException;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pl.edu.pw.pap.common.Constants.ALL;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long courseId) {
        var course = courseService.getById(courseId);
        return course.map(c -> ResponseEntity.ok(addLinks(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/courses")
    public RepresentationModel<CourseDTO> getAllCourses(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = ALL) String language,
            @RequestParam(required = false, defaultValue = ALL) String module,
            @RequestParam(required = false, defaultValue = ALL) String type,
            @RequestParam(required = false, defaultValue = ALL) String level,
            @RequestParam(required = false, defaultValue = "") String teacherName
    ) {
        var courses = courseService.getAllMatchingFilters(name, language, module, type, level, teacherName).stream()
                .map(this::addLinks)
                .toList();

        if (courses.isEmpty()) {
            return HalModelBuilder.emptyHalModel()
                    .embed(Collections.emptyList(), LinkRelation.of("courses"))
                    .build();
        }

        return HalModelBuilder.emptyHalModel()
                .embed(courses, LinkRelation.of("courses"))
                .link(linkTo(methodOn(CourseController.class).getAllCourses(name, language, module, type, level, teacherName)).withSelfRel())
                .build();
    }

    private CourseDTO addLinks(CourseDTO course) {
        return course.add(
                linkTo(methodOn(CourseController.class).getCourseById(course.getId())).withSelfRel(),
                linkTo(methodOn(TeacherController.class).getTeacherById(course.getTeacherId())).withRel("teacher"),
                linkTo(methodOn(CourseController.class).getAllCourses("", ALL, ALL, ALL, ALL, "")).withRel("all"),
                linkTo(methodOn(ReviewController.class).getCourseReviews(course.getId())).withRel("reviews")
        );
    }

    @ExceptionHandler({TeacherNotFoundException.class, CourseNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }
}
