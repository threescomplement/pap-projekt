package pl.edu.pw.pap.comment.report;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.comment.Comment;
import pl.edu.pw.pap.report.GeneralReport;

@Setter
@Getter
@Entity
public class CommentReport extends GeneralReport {
    private Comment reported;
}
