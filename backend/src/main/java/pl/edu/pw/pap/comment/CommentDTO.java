package pl.edu.pw.pap.comment;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
public class CommentDTO extends RepresentationModel<CommentDTO> {
    private Long id;
    private String text;
    private String authorUsername;
    private Timestamp created;
    @JsonIgnore
    private Long courseId;
    private Boolean edited;

}
