package org.development.blogApi.auth.dto.response;

public class ViewMeDto {

    private String userId;
    private String login;
    private String email;

    public ViewMeDto(String userId, String login, String email) {
        this.userId = userId;
        this.login = login;
        this.email = email;
    }

    // Getters and setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
