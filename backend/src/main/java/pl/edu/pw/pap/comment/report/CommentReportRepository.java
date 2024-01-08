package pl.edu.pw.pap.comment.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {
    void deleteAllByCommentId(Long commentId);
}
