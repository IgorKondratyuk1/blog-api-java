package org.development.blogApi.securityDevice.entity;

import jakarta.persistence.*;
import org.development.blogApi.securityDevice.dto.CreateSecurityDeviceDto;

import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "security")
public class SecurityDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "device_id")
    private UUID deviceId;

    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "ip")
    private String ip;

    @Column(name = "title")
    private String title;

    @Column(name = "is_valid")
    private boolean isValid;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_active_date")
    private LocalDateTime lastActiveDate;

    public SecurityDevice(UUID id, UUID deviceId, UUID userId, String ip, String title, boolean isValid, LocalDateTime createdAt, LocalDateTime lastActiveDate) {
        this.id = id;
        this.deviceId = deviceId;
        this.userId = userId;
        this.ip = ip;
        this.title = title;
        this.isValid = isValid;
        this.createdAt = createdAt;
        this.lastActiveDate = lastActiveDate;
    }

    public SecurityDevice() {
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
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

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastActiveDate() {
        return lastActiveDate;
    }

    public void setLastActiveDate(LocalDateTime lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }

    // Static Methods
    private static LocalDateTime calculateLastActiveDate(LocalDateTime createdAt, int expiredDeviceSessionDays) {
        long expirationInMillis = expiredDeviceSessionDays * 24 * 60 * 60 * 1000L;
        Instant instant = Instant.ofEpochMilli(createdAt.toInstant(ZoneOffset.UTC).toEpochMilli() + expirationInMillis);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public static SecurityDevice createInstance(CreateSecurityDeviceDto createSecurityDeviceDto, int expiredDeviceSessionDays) {
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime lastActiveDate = calculateLastActiveDate(createdAt, expiredDeviceSessionDays);
        return new SecurityDevice(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.fromString(createSecurityDeviceDto.getUserId()),
                createSecurityDeviceDto.getIp(),
                createSecurityDeviceDto.getTitle(),
                true,
                createdAt,
                lastActiveDate
        );
    }
}
