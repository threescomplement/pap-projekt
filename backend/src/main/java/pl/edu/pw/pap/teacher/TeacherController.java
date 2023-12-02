package pl.edu.pw.pap.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.pap.course.CourseController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pl.edu.pw.pap.common.Constants.ALL;

@RestController
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;

    @GetMapping("/api/teachers/{id}")
    public ResponseEntity<EntityModel<Teacher>> getTeacherById(@PathVariable Long id) {
        var teacher = teacherService.getTeacherById(id);
        return teacher
                .map(t -> ResponseEntity.ok(addLinks(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/teachers")
    public CollectionModel<EntityModel<Teacher>> getAllTeachers(
            @RequestParam(required = false, defaultValue = "") String name
    ) {
       var teachers = teacherService.getTeachersMatching(name).stream()
               .map(this::addLinks)
               .toList();

       return CollectionModel.of(
               teachers,
               linkTo(methodOn(TeacherController.class).getAllTeachers(name)).withSelfRel()
       );
    }

    private EntityModel<Teacher> addLinks(Teacher teacher) {
        return EntityModel.of(
                teacher,
                linkTo(methodOn(TeacherController.class).getTeacherById(teacher.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAllCourses("", ALL, ALL, ALL, ALL, teacher.getName())).withRel("courses"),
                linkTo(methodOn(TeacherController.class).getAllTeachers("")).withRel("all")
        );
    }
}
