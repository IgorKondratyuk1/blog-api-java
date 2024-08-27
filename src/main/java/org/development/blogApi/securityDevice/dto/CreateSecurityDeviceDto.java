package org.development.blogApi.securityDevice.dto;


public class CreateSecurityDeviceDto {
    private String userId;
    private String ip;
    private String title;

    public CreateSecurityDeviceDto(String userId, String ip, String title) {
        this.userId = userId;
        this.ip = ip;
        this.title = title;
    }

    public CreateSecurityDeviceDto() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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
