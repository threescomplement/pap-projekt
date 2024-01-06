package pl.edu.pw.pap.review.report;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreRemove;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.report.GeneralReport;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.user.User;

@Setter
@Getter
@Entity
public class ReviewReport extends GeneralReport {

    @JsonIgnore
    @ManyToOne
    private Review reported;

    public ReviewReport(User reportingUser, String reason, Review reportedReview){
        super(reportingUser, reason);
        this.reported = reportedReview;
        reportingUser.addReviewReport(this);
        reportedReview.addReport(this);
    }
    protected ReviewReport() {
    }

    @PreRemove
    public void preRemove(){
        if (this.reported != null){
            reported.removeReport(this);
        }
        // TODO add user remove
    }

}
