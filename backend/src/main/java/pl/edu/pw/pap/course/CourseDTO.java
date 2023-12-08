package pl.edu.pw.pap.course;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
public class CourseDTO extends RepresentationModel<CourseDTO> {
    private Long id;
    private String name;
    private String language;
    private String type;
    private String level;
    private String module;
    private Double averageRating;
    private Long teacherId;
}
