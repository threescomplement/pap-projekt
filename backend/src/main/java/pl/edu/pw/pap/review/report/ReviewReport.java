package pl.edu.pw.pap.review.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.report.ReportStatus;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
public class ReviewReport {

    @GeneratedValue
    @Id
    private Long id;


    // TODO model relation with review
    private Long courseId;
    private String reviewerUsername;
    private String reportedText;

    private String reportingUsername;
    private String reason;
    private Boolean resolved;
    private String resolvedByUsername;
    private Timestamp resolvedTimestamp;
    private ReportStatus status;


    public ReviewReport(User reportingUser, String reason, Review reportedReview) {
        this.reason = reason;
        this.reportingUsername = reportingUser.getUsername();
        this.courseId = reportedReview.getCourse().getId();
        this.reviewerUsername = reportedReview.getUser().getUsername();
        this.reportedText = reportedReview.getOpinion();
        this.resolved = false;
        this.resolvedByUsername = null;
        this.resolvedTimestamp = null;
        this.status = ReportStatus.ACTIVE;
    }

    protected ReviewReport() {
    }

    @Override
    public String toString() {
        return "ReviewReport{" +
                "id=" + id +
                ", user=" + reportingUsername +
                ", reason=" + reason +
                ", courseId=" + courseId +
                ", reviewerUsername=" + reviewerUsername +
                '}';
    }


}
