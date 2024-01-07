package pl.edu.pw.pap.review;

public record EditReviewRequest (String text, int easeRating, int interestingRating, int interactiveRating) {
}
