package org.development.blogApi.securityDevice.utils;

import org.development.blogApi.securityDevice.dto.ViewSecurityDeviceDto;
import org.development.blogApi.securityDevice.entity.SecurityDevice;

public class SecurityDeviceMapper {
    public static ViewSecurityDeviceDto toView(SecurityDevice securityDevice) {
        return new ViewSecurityDeviceDto(
                securityDevice.getIp(),
                securityDevice.getTitle(),
                securityDevice.getLastActiveDate().toString(),
                securityDevice.getDeviceId().toString()
        );
    }
}
