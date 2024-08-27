package org.development.blogApi.core.like.dto.response;

public class LikeDetails {
    private String addedAt;
    private String userId;
    private String login;

    public LikeDetails(String addedAt, String userId, String login) {
        this.addedAt = addedAt;
        this.userId = userId;
        this.login = login;
    }

    public String getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(String addedAt) {
        this.addedAt = addedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public String toString() {
        return "LikeDetails{" +
                "addedAt='" + addedAt + '\'' +
                ", userId='" + userId + '\'' +
                ", login='" + login + '\'' +
                '}';
    }
}
