package pl.edu.pw.pap.comment.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.report.GeneralReport;
import pl.edu.pw.pap.user.User;

@Setter
@Getter
@Entity
public class CommentReport extends GeneralReport {

    // We only ever use the ReportDTO class for passing reports so this doesn't matter too much
    @JsonIgnore
    @ManyToOne
    private Comment reported; // TODO model relation with Comment


    public CommentReport(User reportingUser, String reason, Comment reportedComment) {
        super(reportingUser, reason);
        this.reported = reportedComment; // can be deleted if we want, handled in Comment.addReport
        reportedComment.addReport(this);
        reportingUser.addCommentReport(this);
    }

    protected CommentReport() {
    }


}
