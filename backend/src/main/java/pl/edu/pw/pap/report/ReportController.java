package pl.edu.pw.pap.report;


import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.LinkRelation;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.mediatype.hal.HalModelBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import pl.edu.pw.pap.comment.CommentNotFoundException;
import pl.edu.pw.pap.comment.ForbiddenException;
import pl.edu.pw.pap.comment.report.CommentReportNotFoundException;
import pl.edu.pw.pap.review.ReviewNotFoundException;
import pl.edu.pw.pap.review.report.ReviewReportNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.teacher.TeacherNotFoundException;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.util.Collections;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;


@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // oodzielne do dodawania, usuwiania, pobierania pojedynczych (nwm czy potrzebne),
    // wspólny do pobierania wszystkich

    @PostMapping("api/courses/{courseId}/reviews/{username}/reports")
    public ReportDTO reportReview(@PathVariable Long courseId,
                                  @PathVariable String username,
                                  @RequestBody ReportRequest reportRequest,
                                  @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return reportService.reportReview(courseId, username, reportRequest, userPrincipal);
    }

    @PostMapping("api/comments/{commentId}/reports")
    public ReportDTO reportComment(@PathVariable Long commentId,
                                   @RequestBody ReportRequest reportRequest,
                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {

        return reportService.reportComment(commentId, reportRequest, userPrincipal); // links added in service
    }


    @GetMapping("api/admin/reports/all")
    public RepresentationModel<ReportDTO> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return HalModelBuilder.emptyHalModel()
                .embed(reports.isEmpty() ? Collections.emptyList() : reports, LinkRelation.of("reports"))
                .link(linkTo(methodOn(ReportController.class).getAllReports()).withSelfRel())
                .build();
    }

    @GetMapping("api/admin/reports/{resolvedStatus}")
    public RepresentationModel<ReportDTO> getAllReportsByResolvedStatus(@PathVariable(required = false) Boolean resolvedStatus) {
        if (resolvedStatus == null){
            resolvedStatus = false;
        }
        List<ReportDTO> reports = reportService.getReportsByResolved(resolvedStatus);
        return HalModelBuilder.emptyHalModel()
                .embed(reports.isEmpty() ? Collections.emptyList() : reports, LinkRelation.of("reports"))
                .link(linkTo(methodOn(ReportController.class).getAllReports()).withSelfRel())
                .build();
    }


    @DeleteMapping("/api/admin/reports/comments/{commentReportId}")
    public ResponseEntity<ReportDTO> deleteCommentReport(@PathVariable Long commentReportId) {
        reportService.deleteCommentReport(commentReportId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/admin/reports/reviews/{reviewReportId}")
    public ResponseEntity<ReportDTO> deleteReviewReport(@PathVariable Long reviewReportId) {
        reportService.deleteReviewReport(reviewReportId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/reports/comments/{commentReportId}")
    public ReportDTO getCommentReport(@PathVariable Long commentReportId) {
        return reportService.getCommentReport(commentReportId);
    }

    @GetMapping("/api/admin/reports/reviews/{reviewReportId}")
    public ReportDTO getReviewReport(@PathVariable Long reviewReportId) {
        return reportService.getReviewReport(reviewReportId);
    }


    @ExceptionHandler({CommentNotFoundException.class,
            UserNotFoundException.class,
            ReviewNotFoundException.class,
            TeacherNotFoundException.class,
            CommentReportNotFoundException.class,
            ReviewReportNotFoundException.class
    })
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Exception> handleForbidden(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
    }


}
