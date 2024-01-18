package pl.edu.pw.pap.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.CommentController;
import pl.edu.pw.pap.comment.CommentNotFoundException;
import pl.edu.pw.pap.comment.CommentRepository;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.comment.report.CommentReportNotFoundException;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.review.ReviewController;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.review.ReviewNotFoundException;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.report.ReviewReport;
import pl.edu.pw.pap.review.report.ReviewReportNotFoundException;
import pl.edu.pw.pap.review.report.ReviewReportRepository;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.UserNotFoundException;
import pl.edu.pw.pap.user.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReviewReportRepository reviewReportRepository;
    private final CommentReportRepository commentReportRepository;
    private final ReviewRepository reviewRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ReportDTO convertReportToDto(ReviewReport report) {

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReportedText())
                .reportingUsername(report.getReportingUsername())
                .reason(report.getReason())
                .reviewerUsername(report.getReviewerUsername())
                .courseId(report.getCourseId())
                .resolved(report.getResolved())
                .resolvedByUsername(report.getResolvedByUsername())
                .build();

        return reportDTO.add(
                linkTo(methodOn(ReportController.class).getReviewReport(report.getId())).withSelfRel(),
                linkTo(methodOn(ReviewController.class).getReview(report.getCourseId(), report.getReviewerUsername())).withRel("entity"),
                linkTo(methodOn(ReviewController.class).getReview(report.getCourseId(), report.getReviewerUsername())).withRel("review")
        );
    }

    public ReportDTO convertReportToDto(CommentReport report) {

        var reportDTO = ReportDTO.builder()
                .reportedText(report.getReportedText())
                .reportingUsername(report.getReportingUsername())
                .reason(report.getReason())
                .reviewerUsername(report.getReviewerUsername())
                .courseId(report.getCourseId())
                .resolved(report.getResolved())
                .resolvedByUsername(report.getResolvedByUsername())
                .build();
        return reportDTO.add(
                linkTo(methodOn(ReportController.class).getCommentReport(report.getId())).withSelfRel(),
                linkTo(methodOn(CommentController.class).getCommentById(report.getCommentId())).withRel("entity"),
                linkTo(methodOn(ReviewController.class).getReview(report.getCourseId(), report.getReviewerUsername())).withRel("review")
        );
    }

    private List<ReportDTO> concatIntoDtoList(List<CommentReport> commentReports, List<ReviewReport> reviewReports){
        ArrayList<ReportDTO> allReports = new ArrayList<>(reviewReports
                .stream()
                .map(this::convertReportToDto)
                .toList());
        allReports.addAll(commentReports
                .stream()
                .map(this::convertReportToDto)
                .toList());
        return allReports;
    }

    public List<ReportDTO> getAllReports() {
        var reviewReports = reviewReportRepository.findAll();
        var commentReports = commentReportRepository.findAll();
        return concatIntoDtoList(commentReports, reviewReports);
    }

    public List<ReportDTO> getReportsByResolved(Boolean resolvedStatus) {
        var reviewReports = reviewReportRepository.findByResolved(resolvedStatus);
        var commentReports = commentReportRepository.findByResolved(resolvedStatus);
        return concatIntoDtoList(commentReports, reviewReports);
    }

    public void deleteCommentReport(Long commentReportId){
        var maybeReport = commentReportRepository.findById(commentReportId);
        if (maybeReport.isEmpty()){
            return;
        }
        commentReportRepository.delete(maybeReport.get());
    }
    public void deleteReviewReport(Long reviewReportId){
        var maybeReport = reviewReportRepository.findById(reviewReportId);
        if (maybeReport.isEmpty()){
            return;
        }
        reviewReportRepository.delete(maybeReport.get());
    }

    public ReportDTO getReviewReport(Long reviewReportId){
        var reviewReport = reviewReportRepository.findById(reviewReportId)
                .orElseThrow(() -> new ReviewReportNotFoundException("No review report with id " + reviewReportId));
        return convertReportToDto(reviewReport);
    }

    public ReportDTO getCommentReport(Long commentReportId){
        var commentReport = commentReportRepository.findById(commentReportId)
                .orElseThrow(() -> new CommentReportNotFoundException("No report with id:" + commentReportId));
        return convertReportToDto(commentReport);
    }

    public ReportDTO reportReview(Long courseId, String reviewerUsername, ReportRequest reportRequest,  UserPrincipal userPrincipal){
        var reviewer = userRepository.findByUsername(reviewerUsername)
                .orElseThrow(() -> new UserNotFoundException("No usern with username" + reviewerUsername));
        var review = reviewRepository.findById(new ReviewKey(reviewer.getId(), courseId))
                .orElseThrow(() -> new ReviewNotFoundException("No review of course " + courseId + " by " + reviewerUsername));
        var reportingUser = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(()-> new UserNotFoundException("User with username " + userPrincipal.getUsername() + " doesn't exist"));
        var report = reviewReportRepository.save(new ReviewReport(reportingUser, reportRequest.reportReason(), review));

        return convertReportToDto(report);
    }

    public ReportDTO reportComment(Long commentId, ReportRequest reportRequest, UserPrincipal userPrincipal){

        var comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("No comment with id = " + commentId));
        var reportingUser = userRepository.findByUsername(userPrincipal.getUsername())
                .orElseThrow(()-> new UserNotFoundException("User with username " + userPrincipal.getUsername() + " doesn't exist"));

        var report = commentReportRepository.save(new CommentReport(reportingUser, reportRequest.reportReason(), comment));

        return convertReportToDto(report);
    }



}
