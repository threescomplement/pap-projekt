package pl.edu.pw.pap.user.emailverification;

public class EmailVerificationException extends RuntimeException{
    public EmailVerificationException(String message) {
        super(message);
    }
}
