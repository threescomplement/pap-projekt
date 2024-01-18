package pl.edu.pw.pap.comment.report;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.user.User;

@Setter
@Getter
@Entity
public class CommentReport {


    @Id
    @GeneratedValue
    private Long id;
    private String reason; // TODO: change to enum
    private String reportingUsername;

    // TODO: Model a proper relation between CommentReport and Comment
    private Long commentId;
    private String reportedText;
    private String commenterUsername;
    private Long courseId;
    private String reviewerUsername;
    private Boolean resolved;
    private String resolvedByUsername;


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

    }

    protected CommentReport() {
    }
}
