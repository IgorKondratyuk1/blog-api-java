package org.development.blogApi.auth.dto.request;


public class RegisterDto {
    private String login;
    private String password;
    private String email;

    public RegisterDto(String login, String username, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public RegisterDto() {}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
