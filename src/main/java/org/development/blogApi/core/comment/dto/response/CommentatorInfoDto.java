package org.development.blogApi.core.comment.dto.response;

public class CommentatorInfoDto {
    private String userId;
    private String userLogin;

    // Constructor
    public CommentatorInfoDto(String userId, String userLogin) {
        this.userId = userId;
        this.userLogin = userLogin;
    }

    // Getters and setters
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
