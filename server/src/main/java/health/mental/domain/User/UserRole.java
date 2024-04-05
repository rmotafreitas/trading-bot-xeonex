package health.mental.domain.User;

public enum UserRole {
    ADMIN("admin"),
    USER("user"),
    PSICO("psico");


    private String role;


    UserRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "ROLE_"+getRole().toUpperCase();
    }
}
