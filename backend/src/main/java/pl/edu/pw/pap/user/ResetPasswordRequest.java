package pl.edu.pw.pap.user;

public record ResetPasswordRequest(String newPassword, String passwordResetToken) {
}
