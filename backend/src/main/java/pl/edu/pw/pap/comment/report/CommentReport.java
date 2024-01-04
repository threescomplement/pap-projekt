package pl.edu.pw.pap.comment.report;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.report.GeneralReport;
import pl.edu.pw.pap.user.User;

@Setter
@Getter
@Entity
public class CommentReport extends GeneralReport {

    private Comment reported; // TODO model relation with Comment


    public CommentReport(User reportingUser, String reason, Comment reportedComment) {
        super(reportingUser, reason);
        this.reported = reportedComment;
        reportedComment.addReport(this);
        reportingUser.addCommentReport(this);
    }

    protected CommentReport() {
    }


}
