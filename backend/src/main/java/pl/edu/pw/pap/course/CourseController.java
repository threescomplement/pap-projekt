package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.pap.teacher.TeacherController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pl.edu.pw.pap.common.Constants.ALL;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<EntityModel<Course>> getCourseById(@PathVariable Long courseId) {
        var course = courseService.getById(courseId);
        return course.map(c -> ResponseEntity.ok(courseWithLinks(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/courses")
    public CollectionModel<EntityModel<Course>> getAllCourses(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = ALL) String language,
            @RequestParam(required = false, defaultValue = ALL) String module,
            @RequestParam(required = false, defaultValue = ALL) String type,
            @RequestParam(required = false, defaultValue = ALL) String level,
            @RequestParam(required = false, defaultValue = "") String teacherName
    ) {
        var courses = courseService.getAllMatchingFilters(name, language, module, type, level, teacherName).stream()
                .map(this::courseWithLinks)
                .toList();
        return CollectionModel.of(
                courses,
                linkTo(methodOn(CourseController.class).getAllCourses(name, language, module, type, level, teacherName)).withSelfRel()
        );
    }

    private EntityModel<Course> courseWithLinks(Course course) {
        return EntityModel.of(
                course,
                linkTo(methodOn(CourseController.class).getCourseById(course.getId())).withSelfRel(),
                linkTo(methodOn(TeacherController.class).getTeacherById(course.getTeacher().getId())).withRel("teacher"),
                linkTo(methodOn(CourseController.class).getAllCourses("", ALL, ALL, ALL, ALL, "")).withRel("all")
        );
    }

    // TODO add links to reviews
}
