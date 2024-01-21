package pl.edu.pw.pap.comment.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.report.ReportStatus;
import pl.edu.pw.pap.user.User;

import java.sql.Timestamp;

@Setter
@Getter
@Entity
public class CommentReport {


    @Id
    @GeneratedValue
    private Long id;
    private String reason; // TODO: change to enum
    private String reportingUsername;

    private Long commentId;
    private String reportedText;
    private String commenterUsername;
    private Long courseId;
    private String reviewerUsername;
    private Boolean resolved;
    private String resolvedByUsername;
    private Timestamp resolvedTimestamp;
    private ReportStatus status;


    public CommentReport(User reportingUser, String reason, Comment reportedComment) {
        this.reason = reason;
        this.commentId = reportedComment.getId();
        this.reportingUsername = reportingUser.getUsername();
        this.reportedText = reportedComment.getText();
        this.commenterUsername = reportedComment.getUser().getUsername();
        this.courseId = reportedComment.getReview().getCourse().getId();
        this.reviewerUsername = reportedComment.getReview().getUser().getUsername();
        this.resolved = false;
        this.resolvedByUsername = null;
        this.status = ReportStatus.ACTIVE;
        this.resolvedTimestamp = null;
    }

    protected CommentReport() {
    }
}
