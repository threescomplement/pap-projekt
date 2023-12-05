package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.ReviewNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    public List<Comment> getCommentsForReview(Long courseId, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("No user with username " + username) );
        return commentRepository.findByReview_Id(new ReviewKey(user.getId(), courseId));
    }

    public void deleteComment(Long commentId, UserPrincipal principal) {

        var maybeComment = commentRepository.findById(commentId);
        if (maybeComment.isEmpty()){
            return;
        }
        log.info("Trying to get user with username: " + principal.getUsername());
        var maybeUser = userRepository.findByUsername(principal.getUsername());
        if (maybeUser.isEmpty()){
            return;
        }
        var comment = maybeComment.get();
        var user = maybeUser.get();
        if (!comment.getUser().getId().equals(user.getId()) && (!(user.getRole().equals("ROLE_ADMIN") ))) {
            return; // TODO not handling case where user is forbidden from deleting
        }

        commentRepository.delete(comment);
    }

    public Optional<Comment> findCommentById(Long commentId) {
        return commentRepository.findById(commentId);
    }


    public List<Comment> getCommentsByUsername(String username){
        return commentRepository.findCommentsByUser_Username(username);
    }

    public Comment addNewComment(Long courseId, String reviewUsername, AddCommentRequest request, UserPrincipal principal) {
        String text = request.text();
        User reviewUser = userRepository.findByUsername(reviewUsername).orElseThrow(
                () -> new UserNotFoundException("No user found with username: " + principal.getUsername())
        );


        // TODO: change thrown exception to badrequest
        User addingUser = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new UserNotFoundException("No user found with username: " + principal.getUsername()));

        Review review = reviewRepository.findById(new ReviewKey(reviewUser.getId(), courseId)).orElseThrow(
                () -> new ReviewNotFoundException("No existing review of course " + courseId + "by " + reviewUsername));


        Comment comment = new Comment(text, review, addingUser);
        return commentRepository.save(comment);
    }
}
