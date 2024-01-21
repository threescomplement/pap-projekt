package pl.edu.pw.pap.comment;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pl.edu.pw.pap.comment.report.CommentReport;
import pl.edu.pw.pap.comment.report.CommentReportRepository;
import pl.edu.pw.pap.comment.report.ReportStatus;
import pl.edu.pw.pap.review.Review;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.review.ReviewRepository;
import pl.edu.pw.pap.review.ReviewNotFoundException;
import pl.edu.pw.pap.security.UserPrincipal;
import pl.edu.pw.pap.user.User;
import pl.edu.pw.pap.user.UserRepository;
import pl.edu.pw.pap.user.UserNotFoundException;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final CommentReportRepository commentReportRepository;

    public CommentDTO convertToDto(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .authorUsername(comment.getUser().getUsername())
                .text(comment.getText())
                .created(comment.getCreated())
                .courseId(comment.getReview().getCourse().getId())
                .reviewAuthorUsername(comment.getReview().getUser().getUsername())
                .edited(comment.getEdited())
                .build();
    }

    public List<CommentDTO> getCommentsForReview(Long courseId, String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user with username " + username));

        return commentRepository
                .findByReview_Id(new ReviewKey(user.getId(), courseId))
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public void deleteComment(Long commentId, UserPrincipal principal) {

        var maybeComment = commentRepository.findById(commentId);
        if (maybeComment.isEmpty()) {
            return;
        }
        log.info("Trying to get user with username: " + principal.getUsername());
        var maybeUser = userRepository.findByUsername(principal.getUsername());
        if (maybeUser.isEmpty()) {
            return;
        }
        var comment = maybeComment.get();
        var user = maybeUser.get();
        if (!comment.getUser().getId().equals(user.getId()) && !user.isAdmin()) {
            throw (new ForbiddenException(("You are not permitted to delete that comment")));
        }

        // set reports as resolved
        Instant currentTime = Instant.now();
        List<CommentReport> commentReports = commentReportRepository.findByCommentIdAndResolved(commentId, false);
        commentReportRepository.saveAll(commentReports
                .stream()
                .peek(report -> {
                    report.setResolved(true);
                    report.setResolvedByUsername(principal.getUsername());
                    report.setResolvedMethod(ReportStatus.CONTENT_DELETE);
                    // in order for all of them to have the same resolve timestamp
                    report.setResolvedTimestamp(Timestamp.from(currentTime));
                })
                .toList()
        );
        commentRepository.delete(comment);

    }

    public Optional<CommentDTO> findCommentById(Long commentId) {
        return commentRepository
                .findById(commentId)
                .map(this::convertToDto);
    }


    public List<CommentDTO> getCommentsByUsername(String username) {
        userRepository.findByUsername(username).orElseThrow(
                () -> new UserNotFoundException("No user with username: " + username)
        );
        return commentRepository
                .findCommentsByUser_Username(username)
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    public CommentDTO addNewComment(Long courseId, String reviewUsername, AddCommentRequest request, UserPrincipal principal) {
        String text = request.text();
        User reviewUser = userRepository.findByUsername(reviewUsername).orElseThrow(
                () -> new UserNotFoundException("No user found with username: " + principal.getUsername())  // TODO ?
        );


        // TODO: change thrown exception to badrequest
        User addingUser = userRepository.findByUsername(principal.getUsername()).orElseThrow(
                () -> new UserNotFoundException("No user found with username: " + principal.getUsername()));

        Review review = reviewRepository.findById(new ReviewKey(reviewUser.getId(), courseId)).orElseThrow(
                () -> new ReviewNotFoundException("No existing review of course " + courseId + "by " + reviewUsername));


        Comment comment = new Comment(text, review, addingUser);
        comment = commentRepository.save(comment);
        reviewRepository.save(review);
        return convertToDto(comment);
    }

    public CommentDTO updateComment(Long commentId, UpdateCommentRequest request, UserPrincipal principal) {
        // check if user has proper privileges
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("No comment with given ID: " + commentId + " found for edit"));

        if (!comment.getUser().getId().equals(principal.getUserId())) {
            throw (new ForbiddenException(("You are not permitted to edit that comment")));
        }
        comment.setText(request.text());
        comment.setEdited(true);
        return convertToDto(commentRepository.save(comment));
    }
}
