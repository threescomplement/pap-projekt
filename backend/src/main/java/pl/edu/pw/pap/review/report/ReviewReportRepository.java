package pl.edu.pw.pap.review.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    Long removeByCourseIdAndReviewerUsername(Long courseId, String reviewerUsername);

    List<ReviewReport> findByResolved(Boolean resolvedStatus);
    List<ReviewReport> findByCourseIdAndReviewerUsernameAndResolved(Long courseId, String reviewerUsername, Boolean resolvedStatus);
}
