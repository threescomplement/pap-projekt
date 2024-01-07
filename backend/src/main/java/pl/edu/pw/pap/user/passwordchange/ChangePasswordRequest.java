package pl.edu.pw.pap.user.passwordchange;

public record ChangePasswordRequest(String oldPassword, String newPassword) {
}
