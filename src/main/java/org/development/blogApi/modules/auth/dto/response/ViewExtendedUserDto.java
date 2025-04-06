package org.development.blogApi.modules.auth.dto.response;

public class ViewExtendedUserDto {

    private String id;
    private String login;
//    private ViewBanExtendedInfoDto banInfo;

    public ViewExtendedUserDto(String id, String login
//                           ViewBanExtendedInfoDto banInfo
    ) {
        this.id = id;
        this.login = login;
//        this.banInfo = banInfo;
    }

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

//    public ViewBanExtendedInfoDto getBanInfo() { return banInfo; }
//    public void setBanInfo(ViewBanExtendedInfoDto banInfo) { this.banInfo = banInfo; }
}
