package pl.edu.pw.pap.comment;

public class commentNotFoundException extends RuntimeException {
    public commentNotFoundException(String message) {
        super(message);
    }
}
