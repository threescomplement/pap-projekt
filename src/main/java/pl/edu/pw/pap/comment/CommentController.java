package pl.edu.pw.pap.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.pw.pap.review.ReviewKey;
import pl.edu.pw.pap.user.UserController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
public class CommentController {
    private final CommentRepository commentRepository;


    @GetMapping("/api/courses/{courseId}/reviews/{reviewerId}/comments/{commentatorId}")
    public EntityModel<Comment> getComment(@PathVariable Long courseId, @PathVariable Long reviewerId, @PathVariable Long commentatorId) {
        var comment = commentRepository.findByReview_IdAndUser_Id(new ReviewKey(reviewerId, courseId), commentatorId).get();
        return EntityModel.of(
                comment,
                linkTo(methodOn(CommentController.class).getComment(courseId, reviewerId, commentatorId)).withSelfRel()
        );
    }
}
