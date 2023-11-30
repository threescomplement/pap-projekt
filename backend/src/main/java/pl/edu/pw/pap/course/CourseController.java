package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<EntityModel<Course>> getCourseById(@PathVariable Long courseId) {
        var course = courseService.getById(courseId);
        return course.map(c -> ResponseEntity.ok(EntityModel.of(
                c,
                linkTo(methodOn(CourseController.class).getCourseById(c.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAllCourses()).withRel("all")
        )))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/courses")
    public CollectionModel<EntityModel<Course>> getAllCourses() {
        var courses = courseService.getAll().stream()
                .map(this::courseWithLinks)
                .toList();

        return CollectionModel.of(
                courses,
                linkTo(methodOn(CourseController.class).getAllCourses()).withSelfRel()
        );
    }

    private EntityModel<Course> courseWithLinks(Course course) {
        return EntityModel.of(
                course,
                linkTo(methodOn(CourseController.class).getCourseById(course.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAllCourses()).withRel("all")
        );
    }
}
