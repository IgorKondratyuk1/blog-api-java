package org.development.blogApi.securityDevice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ViewSecurityDeviceDto {
    private String ip;
    private String title;
    private LocalDateTime lastActiveDate;
    private String deviceId;
}

