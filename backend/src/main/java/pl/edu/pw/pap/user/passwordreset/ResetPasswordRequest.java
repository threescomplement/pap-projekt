package pl.edu.pw.pap.user.passwordreset;

public record ResetPasswordRequest(String newPassword, String passwordResetToken) {
}
