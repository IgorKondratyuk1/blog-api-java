package org.development.blogApi.modules.securityDevice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class CreateSecurityDeviceDto {
    private String userId;
    private String ip;
    private String title;
}
