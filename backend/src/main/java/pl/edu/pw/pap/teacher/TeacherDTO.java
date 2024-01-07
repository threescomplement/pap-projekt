package pl.edu.pw.pap.teacher;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class TeacherDTO extends RepresentationModel<TeacherDTO> {
    private Long id;
    private String name;
    private Double averageRating;
    private Integer numberOfRatings;
}
