package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public List<Comment> getCommentsForReview(Long courseId, Long reviewerId) {
        return commentRepository.findByReview_Id(new ReviewKey(reviewerId, courseId));
    }

    public void deleteComment(Long commentId, UserPrincipal principal) {
        var comment = commentRepository.findById(commentId).orElseThrow();
        var user = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        if (!comment.getUser().getId().equals(user.getId())) {
            // TODO: handle exception in CommentController
            throw new commentNotFoundException("User can only delete his own comments");
        }

        commentRepository.delete(comment);
    }

    public Optional<Comment> findCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }


    public List<Comment> getCommentsByUsername(String username){
        return commentRepository.findCommentsByUser_Username(username);
    }

}
