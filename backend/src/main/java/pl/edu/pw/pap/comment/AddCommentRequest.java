package pl.edu.pw.pap.comment;

public record AddCommentRequest(String text, Long courseId, String username) {
}
