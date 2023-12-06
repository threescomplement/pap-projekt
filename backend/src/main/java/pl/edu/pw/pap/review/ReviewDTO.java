package pl.edu.pw.pap.review;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.Builder;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReviewDTO extends RepresentationModel<ReviewDTO> {
    private String authorUsername;
    private String opinion;
    private Integer overallRating;
    private Timestamp created;
    @JsonIgnore
    private Long courseId;
}