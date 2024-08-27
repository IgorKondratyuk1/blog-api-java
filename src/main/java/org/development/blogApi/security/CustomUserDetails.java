package org.development.blogApi.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;

public class CustomUserDetails extends User {

    private String userId;
    private String deviceId;
    private LocalDateTime lastActiveDate;

    public CustomUserDetails(UserDetails user, String userId, String deviceId, LocalDateTime lastActiveDate) {
        super(user.getUsername(), user.getPassword(), user.isEnabled(), user.isAccountNonExpired(), user.isAccountNonExpired(), user.isAccountNonLocked(), user.getAuthorities());
        this.userId = userId;
        this.deviceId = deviceId;
        this.lastActiveDate = lastActiveDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public LocalDateTime getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDateTime lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append(" [");
        sb.append("Username=").append(this.getUsername()).append(", ");
        sb.append("Password=[PROTECTED], ");
        sb.append("Enabled=").append(this.isEnabled()).append(", ");
        sb.append("AccountNonExpired=").append(this.isAccountNonExpired()).append(", ");
        sb.append("CredentialsNonExpired=").append(this.isCredentialsNonExpired()).append(", ");
        sb.append("AccountNonLocked=").append(this.isAccountNonLocked()).append(", ");
        sb.append("Granted Authorities=").append(this.getAuthorities()).append(", ");
        sb.append("Granted UserId=").append(this.getUserId()).append(", ");
        sb.append("Granted DeviceId=").append(this.getDeviceId()).append(", ");
        sb.append("Granted LastActiveDate=").append(this.getLastActiveDate()).append("]");
        return sb.toString();
    }
}
