package pl.edu.pw.pap.user;

public record UpdateUserRequest(String username, String email, String role, Boolean enabled) {
}
