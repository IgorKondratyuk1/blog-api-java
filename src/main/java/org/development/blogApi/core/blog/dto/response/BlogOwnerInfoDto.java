package org.development.blogApi.core.blog.dto.response;

public class BlogOwnerInfoDto {

    private String userId;
    private String userLogin;

    // Constructor
    public BlogOwnerInfoDto(String userId, String userLogin) {
        this.userId = userId;
        this.userLogin = userLogin;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }
}
