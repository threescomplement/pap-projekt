package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.review.ReviewKey;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsForReview(Long courseId, Long reviewerId) {
        return commentRepository.findByReview_Id(new ReviewKey(reviewerId, courseId));
    }

}
