package pl.edu.pw.pap.review;

public class DuplicateReviewException extends RuntimeException {
    DuplicateReviewException(String message) { super(message); }
}
