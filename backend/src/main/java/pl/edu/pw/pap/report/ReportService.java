package pl.edu.pw.pap.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.review.report.ReviewReport;
import pl.edu.pw.pap.review.report.ReviewReportRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReviewReportRepository reviewReportRepostiory;
    private final CommentReportRepository commentReportRepository;

    public ReportDTO convertReviewRaportToDto(ReviewReport report) {

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReported().getOpinion())
                .reportingUsername(report.getReportingUser().getUsername())
                .reason(report.getReason())
                .build();

        // TODO: Add links once controller is done
        var review = report.getReported();
        return reportDTO.add(
                linkTo(methodOn(ReportController.class).getReviewReport(report.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getReview(review.getCourse().getId(), review.getUser().getUsername())).withRel("entity"),
                linkTo(methodOn(ReviewController.class).getReview(review.getCourse().getId(), review.getUser().getUsername())).withRel("review")
        );
    }

    public ReportDTO convertCommentRaportToDto(CommentReport report) {

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReported().getText())
                .reportingUsername(report.getReportingUser().getUsername())
                .reason(report.getReason())
                .build();
        var review = report.getReported().getReview();
        // TODO: Add links once controller is done
        return reportDTO.add(
                linkTo(methodOn(ReportController.class).getCommentReport(report.getId())).withSelfRel(),
                linkTo(methodOn(CommentController.class).getCommentById(report.getReported().getId())).withRel("entity"),
                linkTo(methodOn(ReviewController.class).getReview(review.getCourse().getId(), review.getUser().getUsername())).withRel("review")
        );
    }


    public List<ReportDTO> getAllReports() {
        var reviewReports = reviewReportRepostiory.findAll();
        var commentReports = commentReportRepository.findAll();
        ArrayList<ReportDTO> allReports = new ArrayList<>(reviewReports
                .stream()
                .map(this::convertReviewRaportToDto)
                .toList());
        allReports.addAll(commentReports
                .stream()
                .map(this::convertCommentRaportToDto)
                .toList());
        return allReports.stream().toList();
    }
}
