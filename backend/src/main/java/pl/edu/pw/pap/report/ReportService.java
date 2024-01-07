package pl.edu.pw.pap.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.review.report.ReviewReport;
import pl.edu.pw.pap.review.report.ReviewReportRepository;
import pl.edu.pw.pap.security.UserPrincipal;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReviewReportRepository reviewReportRepository;
    private final CommentReportRepository commentReportRepository;

    public ReportDTO convertReviewReportToDto(ReviewReport report) {

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReportedText())
                .reportingUsername(report.getReportingUsername())
                .reason(report.getReason())
                .build();

        return reportDTO.add(
                linkTo(methodOn(ReportController.class).getReviewReport(report.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getReview(report.getCourseId(), report.getReviewerUsername())).withRel("entity"),
                linkTo(methodOn(ReviewController.class).getReview(report.getCourseId(), report.getReviewerUsername())).withRel("review")
        );
    }

    public ReportDTO convertCommentReportToDto(CommentReport report) {

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReportedText())
                .reportingUsername(report.getReportingUsername())
                .reason(report.getReason())
                .build();
        return reportDTO.add(
                linkTo(methodOn(ReportController.class).getCommentReport(report.getId())).withSelfRel(),
                linkTo(methodOn(CommentController.class).getCommentById(report.getCommentId())).withRel("entity"),
                linkTo(methodOn(ReviewController.class).getReview(report.getCourseId(), report.getReviewerUsername())).withRel("review")
        );
    }


    public List<ReportDTO> getAllReports() {
        var reviewReports = reviewReportRepository.findAll();
        var commentReports = commentReportRepository.findAll();
        ArrayList<ReportDTO> allReports = new ArrayList<>(reviewReports
                .stream()
                .map(this::convertReviewReportToDto)
                .toList());
        allReports.addAll(commentReports
                .stream()
                .map(this::convertCommentReportToDto)
                .toList());
        return allReports.stream().toList();
    }

    public void deleteCommentReport(Long commentReportId){
        return;
    }
    public void deleteReviewReport(Long commentReportId){
        return;
    }

    public ReportDTO getReviewReport(Long reviewReportId){
        return null;
    }

    public ReportDTO getCommentReport(Long commentReportId){
        return null;
    }

    public ReportDTO reportReview(Long courseId, String reviewerUsername, UserPrincipal userPrincipal){
        return null;
    }

    public ReportDTO reportComment(Long commentId, UserPrincipal userPrincipal){
        return null;
    }


}
