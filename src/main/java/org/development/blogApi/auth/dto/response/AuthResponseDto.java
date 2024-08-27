package org.development.blogApi.auth.dto.response;


public class AuthResponseDto {
    private String accessToken;

    public AuthResponseDto() {}

    public AuthResponseDto(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
