package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.reviewNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.userNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

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

    public Comment addNewComment(AddCommentRequest request) {
        String text = request.text();
        Long courseId = request.courseId();
        String username = request.username();
        Optional<User> getUser = userRepository.findByUsername(username);
        if (getUser.isEmpty()) {
            throw new userNotFoundException("No user found with username: " + username);
        }
        User user = getUser.get();
        Optional<Review> getReview = reviewRepository.findById(new ReviewKey(user.getId(), courseId));
        if (getReview.isEmpty()){
            throw new reviewNotFoundException("No existing review of course " + courseId + "by " + username);
        }
        Review review = getReview.get();

        Comment comment = new Comment(text, review, user);
        return commentRepository.save(comment);
    }
}
