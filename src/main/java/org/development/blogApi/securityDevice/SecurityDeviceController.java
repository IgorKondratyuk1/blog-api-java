package org.development.blogApi.securityDevice;

import jakarta.servlet.http.HttpServletRequest;
import org.development.blogApi.infrastructure.security.JwtService;
import org.development.blogApi.securityDevice.dto.ViewSecurityDeviceDto;
import org.development.blogApi.securityDevice.entity.SecurityDevice;
import org.development.blogApi.securityDevice.utils.SecurityDeviceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/security/devices")
public class SecurityDeviceController {
    private final SecurityDeviceService securityDeviceService;
    private final JwtService jwtService;

    public SecurityDeviceController(SecurityDeviceService securityDeviceService, JwtService jwtService) {
        this.securityDeviceService = securityDeviceService;
        this.jwtService = jwtService;
    }

    @GetMapping("")
    public List<ViewSecurityDeviceDto> findAllSecurityDevices(HttpServletRequest request) {
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
        String userId = this.jwtService.extractUserId(refreshToken);
        List<SecurityDevice> result = securityDeviceService.getAllDeviceSessions(userId);
        if (result == null || result.isEmpty()) {
            return new ArrayList<ViewSecurityDeviceDto>();
        }

        return result.stream()
                .map(SecurityDeviceMapper::toView)
                .toList();
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAllSecurityDevicesExceptCurrent(HttpServletRequest request) {
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
        String userId = this.jwtService.extractUserId(refreshToken);
        String deviceId = this.jwtService.extractDeviceId(refreshToken);
        securityDeviceService.deleteOtherDeviceSessions(userId, deviceId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSecurityDeviceById(@PathVariable("id") UUID deviceId, HttpServletRequest request) {
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
        UUID userId = UUID.fromString(this.jwtService.extractUserId(refreshToken));
        securityDeviceService.deleteDeviceSession(userId, deviceId);
    }
}
