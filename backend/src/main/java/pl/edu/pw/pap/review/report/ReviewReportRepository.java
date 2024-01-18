package pl.edu.pw.pap.review.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    Long removeByCourseIdAndReviewerUsername(Long courseId, String reviewerUsername);

    List<ReviewReport> findByResolved(Boolean resolvedStatus);
}
