package org.development.blogApi.securityDevice;

import jakarta.servlet.http.HttpServletRequest;
import org.development.blogApi.security.JwtService;
import org.development.blogApi.securityDevice.dto.ViewSecurityDeviceDto;
import org.development.blogApi.securityDevice.entity.SecurityDevice;
import org.development.blogApi.securityDevice.utils.SecurityDeviceMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/security/devices")
public class SecurityDevicesController {

    private final SecurityDevicesService securityDevicesService;
    private final JwtService jwtService;

    public SecurityDevicesController(SecurityDevicesService securityDevicesService, JwtService jwtService) {
        this.securityDevicesService = securityDevicesService;
        this.jwtService = jwtService;
    }

    @GetMapping
    public ResponseEntity<List<ViewSecurityDeviceDto>> findAllSecurityDevices(HttpServletRequest request) {  // TODO check what is the ResponseEntity and maybe delete from response
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
        System.out.println(refreshToken);
        String userId = this.jwtService.extractUserId(refreshToken);

        // Token Payload
        List<SecurityDevice> result = securityDevicesService.getAllDeviceSessions(userId);

        if (result == null || result.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<ViewSecurityDeviceDto> viewSecurityDeviceDto = result.stream()
                .map(SecurityDeviceMapper::toView)
                .toList();

        return new ResponseEntity<>(viewSecurityDeviceDto, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSecurityDevicesExceptCurrent(HttpServletRequest request) {
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
        String userId = this.jwtService.extractUserId(refreshToken);
        String deviceId = this.jwtService.extractDeviceId(refreshToken);

        System.out.println(refreshToken);
        System.out.println(userId);
        System.out.println(deviceId);

        securityDevicesService.deleteOtherDeviceSessions(userId, deviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSecurityDeviceById(@PathVariable("id") String deviceId, HttpServletRequest request) {
        String refreshToken = this.jwtService.getJwtRefreshFromCookies(request);
        String userId = this.jwtService.extractUserId(refreshToken);

        System.out.println("refreshToken: " + refreshToken + "\nuserId: " + userId + "\ndeviceId: " + deviceId);

        securityDevicesService.deleteDeviceSession(userId, deviceId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
