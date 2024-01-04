package pl.edu.pw.pap.review.report;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.report.GeneralReport;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

@Setter
@Getter
@Entity
public class ReviewReport extends GeneralReport {


    private Review reported; // TODO model relation with Review

    public ReviewReport(User reportingUser, String reason, Review reportedReview){
        super(reportingUser, reason);
        this.reported = reportedReview;
        reportingUser.addReviewReport(this);
        reportedReview.addReport(this);
    }
    protected ReviewReport() {
    }

}
