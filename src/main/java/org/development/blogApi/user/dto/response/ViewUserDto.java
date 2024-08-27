package org.development.blogApi.user.dto.response;

import java.time.LocalDateTime;

public class ViewUserDto {

    private String id;
    private String login;
    private String email;
    private LocalDateTime createdAt;

    // Constructor
    public ViewUserDto(String id, String login, String email, LocalDateTime createdAt) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
