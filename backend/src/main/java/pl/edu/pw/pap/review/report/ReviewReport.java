package pl.edu.pw.pap.review.report;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import pl.edu.pw.pap.report.GeneralReport;
import pl.edu.pw.pap.review.Review;

@Setter
@Getter
@Entity
public class ReviewReport extends GeneralReport {


    private Review reported;

}
