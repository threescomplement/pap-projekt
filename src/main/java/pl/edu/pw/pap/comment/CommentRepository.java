package pl.edu.pw.pap.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.pap.review.ReviewKey;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReview_Id(ReviewKey id);
}
