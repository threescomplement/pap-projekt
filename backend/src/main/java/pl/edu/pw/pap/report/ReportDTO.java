package pl.edu.pw.pap.report;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;


@Data
@Builder
@EqualsAndHashCode(callSuper = true)
public class ReportDTO extends RepresentationModel<ReportDTO>{

    private String reportingUsername;
    private String reportedText;
    private String reason;
    private Long courseId;
    private String reviewerUsername;
    private Boolean resolved;
    private String resolvedByUsername;
    private Timestamp resolvedTimestamp;
    private String status;
}
