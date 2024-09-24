package org.development.blogApi.securityDevice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.development.blogApi.securityDevice.dto.CreateSecurityDeviceDto;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
        LocalDateTime createdAt = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);
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
