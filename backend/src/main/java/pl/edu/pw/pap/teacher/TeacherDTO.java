package pl.edu.pw.pap.teacher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
@AllArgsConstructor
public class TeacherDTO extends RepresentationModel<TeacherDTO> {
    private Long id;
    private String name;
    private Double averageEaseRating;
    private Double averageInterestRating;
    private Double averageInteractiveRating;
}
