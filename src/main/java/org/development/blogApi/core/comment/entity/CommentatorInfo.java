package org.development.blogApi.core.comment.entity;

public class CommentatorInfo {
    private String userId;
    private String userLogin;

    public CommentatorInfo(String userId, String userLogin) {
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
