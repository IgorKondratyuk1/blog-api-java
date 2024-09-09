package org.development.blogApi.e2e.helpers.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class TestUserData {
    private UUID id;
    private String email;
    private String username;
    private String password;
    private UUID confirmationCode;
    private UUID recoveryCode;

    private String accessToken;
    private String refreshToken;

    public TestUserData(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
}
