package pl.edu.pw.pap.course;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<EntityModel<Course>> getCourseById(@PathVariable Long courseId) {
        var course = courseService.getById(courseId);
        return course.map(value -> ResponseEntity.ok(EntityModel.of(
                value,
                linkTo(methodOn(CourseController.class).getCourseById(courseId)).withSelfRel()
                ))).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
