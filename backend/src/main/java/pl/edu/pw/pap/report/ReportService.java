package pl.edu.pw.pap.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.review.report.ReviewReport;
import pl.edu.pw.pap.review.report.ReviewReportRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReviewReportRepository reviewReportRepostiory;
    private final CommentReportRepository commentReportRepository;

    public ReportDTO convertReviewRaportToDto(ReviewReport report){

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReported().getOpinion())
                .reportingUsername(report.getReportingUser().getUsername())
                .reason(report.getReason())
                .build();

        // TODO: Add links once controller is done
        return reportDTO.add();
    }

}
