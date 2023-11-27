package pl.edu.pw.pap.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.edu.pw.pap.review.ReviewKey;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByReview_Id(ReviewKey id);

    Optional<Comment> findByReview_IdAndUser_Id(ReviewKey reviewKey, Long userId);
}
