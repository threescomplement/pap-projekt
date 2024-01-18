package pl.edu.pw.pap.comment.report;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentReportRepository extends JpaRepository<CommentReport, Long> {

    Long removeByCommentId(Long commentId);

    List<CommentReport> findByResolved(Boolean resolvedStatus);
}
