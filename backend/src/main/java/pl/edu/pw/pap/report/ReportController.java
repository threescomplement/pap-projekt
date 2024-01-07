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
import pl.edu.pw.pap.comment.UnauthorizedException;
import pl.edu.pw.pap.review.DuplicateReviewException;
import pl.edu.pw.pap.review.ReviewNotFoundException;
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
        return null;
    }

    @PostMapping("api/comments/{commentId}/reports")
    public ReportDTO reportComment(@PathVariable Long commentId,
                                   @RequestBody ReportRequest reportRequest,
                                   @AuthenticationPrincipal UserPrincipal userPrincipal) {
        return null;
    }


    @GetMapping("api/admin/reports")
    public RepresentationModel<ReportDTO> getAllReports() {
        List<ReportDTO> reports = reportService.getAllReports();
        return HalModelBuilder.emptyHalModel()
                .embed(reports.isEmpty() ? Collections.emptyList() : reports, LinkRelation.of("reports"))
                .link(linkTo(methodOn(ReportController.class).getAllReports()).withSelfRel())
                .build();
    }

    @GetMapping("/api/admin/reports/comments/{commentReportId}")
    public ReportDTO getCommentReport(@PathVariable Long commentReportId) {
        return null;
    }

    @GetMapping("/api/admin/reports/reviews/{reviewReportId}")
    public ReportDTO getReviewReport(@PathVariable Long reviewReportId) {
        return null;
    }


    @DeleteMapping("/api/admin/reports/comments/{commentReportId}")
    public ResponseEntity<ReportDTO> deleteCommentReport(@PathVariable Long commentReportId) {
        return null;
    }

    @DeleteMapping("/api/admin/reports/reviews/{reviewReportId}")
    public ResponseEntity<ReportDTO> deleteReviewReport(@PathVariable Long reviewReportId) {
        return null;
    }


    @ExceptionHandler({CommentNotFoundException.class, UserNotFoundException.class, ReviewNotFoundException.class, TeacherNotFoundException.class})
    public ResponseEntity<Exception> handleEntityNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e);
    }


    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<Exception> handleForbidden(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e);
    }

    @ExceptionHandler(DuplicateReviewException.class)
    public ResponseEntity<Exception> handleDuplicateReview(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    }


}
