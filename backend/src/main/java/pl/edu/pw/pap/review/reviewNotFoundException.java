package pl.edu.pw.pap.review;

public class reviewNotFoundException extends RuntimeException {
    public reviewNotFoundException(String message) {
        super(message);
    }
}
