package pl.edu.pw.pap.teacher;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.pap.course.CourseController;
import pl.edu.pw.pap.course.CourseDTO;
import pl.edu.pw.pap.course.CourseService;

import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static pl.edu.pw.pap.common.Constants.ALL;

@RestController
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private final CourseService courseService;

    @GetMapping("/api/teachers/{id}")
    public ResponseEntity<TeacherDTO> getTeacherById(@PathVariable Long id) {
        var teacher = teacherService.getTeacherById(id);
        return teacher
                .map(t -> ResponseEntity.ok(addLinks(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/teachers")
    public RepresentationModel<TeacherDTO> getAllTeachers(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = ALL) String language
    ) {
        var teachers = teacherService.getTeachersMatchingFilters(name, language).stream()
                .map(this::addLinks)
                .toList();

        return HalModelBuilder.emptyHalModel()
                .embed(teachers.isEmpty() ? Collections.emptyList() : teachers, LinkRelation.of("teachers"))
                .link(linkTo(methodOn(TeacherController.class).getAllTeachers(name, language)).withSelfRel())
                .build();
    }

    @GetMapping("/api/teachers/{teacherId}/courses")
    public RepresentationModel<CourseDTO> getTeacherCourses(@PathVariable Long teacherId){
        var courses = courseService.getTeacherCourses(teacherId);

        if (courses.isEmpty()) {
            return HalModelBuilder.emptyHalModel()
                    .embed(Collections.emptyList(), LinkRelation.of("courses"))
                    .build();
        }

        return HalModelBuilder.emptyHalModel()
                .embed(courses, LinkRelation.of("courses"))
                .link(linkTo(methodOn(TeacherController.class).getTeacherCourses(teacherId)).withSelfRel())
                .link(linkTo(methodOn(TeacherController.class).getTeacherById(teacherId)).withRel("teacher"))
                .build();
    }

    private TeacherDTO addLinks(TeacherDTO teacher) {
        return teacher.add(
                linkTo(methodOn(TeacherController.class).getTeacherById(teacher.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getAllCourses("", ALL, ALL, ALL, ALL, teacher.getName())).withRel("courses"),
                linkTo(methodOn(TeacherController.class).getAllTeachers("", ALL)).withRel("all")  // TODO review links
        );
    }
}
