package org.development.blogApi.securityDevice.dto;

public class ViewSecurityDeviceDto {
    private String ip;
    private String title;
    private String lastActiveDate;
    private String deviceId;

    // Default constructor
    public ViewSecurityDeviceDto() {}

    // Parameterized constructor
    public ViewSecurityDeviceDto(String ip, String title, String lastActiveDate, String deviceId) {
        this.ip = ip;
        this.title = title;
        this.lastActiveDate = lastActiveDate;
        this.deviceId = deviceId;
    }

    // Getters and Setters
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

    public String getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(String lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}

