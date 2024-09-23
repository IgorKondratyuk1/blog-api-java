package org.development.blogApi.securityDevice;

import org.development.blogApi.securityDevice.dto.CreateSecurityDeviceDto;
import org.development.blogApi.securityDevice.entity.SecurityDevice;
import org.development.blogApi.securityDevice.repository.SecurityDeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class SecurityDevicesService {

    @Value("${device-session.expiration-days}")
    private int expiredDeviceSession;

    private final SecurityDeviceRepository securityDevicesRepository;

    @Autowired
    public SecurityDevicesService(SecurityDeviceRepository securityDevicesRepository) {
        this.securityDevicesRepository = securityDevicesRepository;
    }

    public SecurityDevice createDeviceSession(CreateSecurityDeviceDto createSecurityDeviceDto) {
        SecurityDevice newDeviceSession = SecurityDevice.createInstance(
                createSecurityDeviceDto,
                expiredDeviceSession
        );

        return securityDevicesRepository.save(newDeviceSession);
    }

    public SecurityDevice findDeviceSessionByDeviceId(String deviceId) {
        return securityDevicesRepository.findByDeviceId(UUID.fromString(deviceId)).orElseThrow(() -> new RuntimeException("No security device founded"));
    }

    public List<SecurityDevice> getAllDeviceSessions(String userId) {
        return securityDevicesRepository.findAllByUserId(UUID.fromString(userId));
    }

    public void deleteOtherDeviceSessions(String userId, String deviceId) {
        securityDevicesRepository.deleteOtherSessionsExceptCurrent(UUID.fromString(userId), UUID.fromString(deviceId));
    }

    public void deleteAllUserSessions(String userId) {
        securityDevicesRepository.deleteAllByUserId(UUID.fromString(userId));
    }

    @Transactional
    public void deleteDeviceSession(String userId, String deviceId) {
        SecurityDevice deviceSession = securityDevicesRepository.findByDeviceId(UUID.fromString(deviceId))
                .orElseThrow(() -> new RuntimeException("Session is not found"));

        if (!userId.equals(deviceSession.getUserId().toString())) {
            throw new RuntimeException("Cannot delete session of other user");
        }

        securityDevicesRepository.deleteById(UUID.fromString(deviceId));
    }
}
