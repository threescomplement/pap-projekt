package pl.edu.pw.pap.review.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.report.GeneralReport;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

@Setter
@Getter
@Entity
public class ReviewReport {

    @GeneratedValue
    @Id
    Long id;

    Long courseId;
    String reviewerUsername;

    String reportingUsername;
    String reason;
    public ReviewReport(User reportingUser, String reason, Review reportedReview) {
        this.reason = reason;
        this.reportingUsername = reportingUser.getUsername();
        this.courseId = reportedReview.getCourse().getId();
        this.reviewerUsername = reportedReview.getUser().getUsername();
        reportedReview.addReport(this);
        reportingUser.addReviewReport(this);
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
