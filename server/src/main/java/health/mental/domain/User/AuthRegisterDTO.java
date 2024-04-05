package health.mental.domain.User;

public record AuthRegisterDTO(String login, String password,UserRole role) {
}
