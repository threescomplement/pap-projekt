package pl.edu.pw.pap.review;

public record AddReviewRequest (String text, int rating, Long courseId, String username ){
}