package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/api/courses/{courseId}/reviews/{reviewerId}/comments")
    public List<Comment> getCommentsForReview(@PathVariable Long courseId, @PathVariable Long reviewerId) {
        var comments = commentService.getCommentsForReview(courseId, reviewerId);
        return comments;
    }
}
