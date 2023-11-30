package pl.edu.pw.pap.course;

public class courseNotFoundException extends RuntimeException {
    public courseNotFoundException(String message) {
        super(message);
    }
}
