package org.development.blogApi.auth.dto;

public class ExtendedLoginDataDto {
    private String loginOrEmail;
    private String password;
    private String ip;
    private String title;

    public ExtendedLoginDataDto(String loginOrEmail, String password, String ip, String title) {
        this.loginOrEmail = loginOrEmail;
        this.password = password;
        this.ip = ip;
        this.title = title;
    }

    public String getLoginOrEmail() {
        return loginOrEmail;
    }

    public void setLoginOrEmail(String loginOrEmail) {
        this.loginOrEmail = loginOrEmail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
