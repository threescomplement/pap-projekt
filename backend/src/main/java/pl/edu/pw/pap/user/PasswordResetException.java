package pl.edu.pw.pap.user;

public class PasswordResetException extends RuntimeException {
    public PasswordResetException(String message) {
        super(message);
    }
}
