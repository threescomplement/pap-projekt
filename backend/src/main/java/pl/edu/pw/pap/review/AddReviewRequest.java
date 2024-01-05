package pl.edu.pw.pap.review;

public record AddReviewRequest (String text, int easeRating, int interestingRating, int interactiveRating){
}
