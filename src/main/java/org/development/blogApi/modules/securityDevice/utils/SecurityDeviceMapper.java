package org.development.blogApi.modules.securityDevice.utils;

import org.development.blogApi.modules.securityDevice.dto.ViewSecurityDeviceDto;
import org.development.blogApi.modules.securityDevice.entity.SecurityDevice;

public class SecurityDeviceMapper {
    public static ViewSecurityDeviceDto toView(SecurityDevice securityDevice) {
        return new ViewSecurityDeviceDto(
                securityDevice.getIp(),
                securityDevice.getTitle(),
                securityDevice.getLastActiveDate(),
                securityDevice.getDeviceId().toString()
        );
    }
}
