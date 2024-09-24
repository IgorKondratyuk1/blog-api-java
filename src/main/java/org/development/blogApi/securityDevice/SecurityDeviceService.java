package org.development.blogApi.securityDevice;

import org.development.blogApi.exceptions.securityDeviceExceptions.SecurityDeviceForbiddenException;
import org.development.blogApi.exceptions.securityDeviceExceptions.SecurityDeviceNotFoundException;
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
public class SecurityDeviceService {

    @Value("${device-session.expiration-days}")
    private int expiredDeviceSession;

    private final SecurityDeviceRepository securityDeviceRepository;

    @Autowired
    public SecurityDeviceService(SecurityDeviceRepository securityDeviceRepository) {
        this.securityDeviceRepository = securityDeviceRepository;
    }

    public SecurityDevice createDeviceSession(CreateSecurityDeviceDto createSecurityDeviceDto) {
        SecurityDevice newDeviceSession = SecurityDevice.createInstance(
                createSecurityDeviceDto,
                expiredDeviceSession
        );

        return securityDeviceRepository.save(newDeviceSession);
    }

    public SecurityDevice findDeviceSessionByDeviceId(String deviceId) {
        return securityDeviceRepository.findByDeviceId(UUID.fromString(deviceId)).orElseThrow(() -> new SecurityDeviceNotFoundException());
    }

    public List<SecurityDevice> getAllDeviceSessions(String userId) {
        return securityDeviceRepository.findAllByUserId(UUID.fromString(userId));
    }

    @Transactional
    public void deleteOtherDeviceSessions(String userId, String deviceId) {
        securityDeviceRepository.deleteOtherSessionsExceptCurrent(UUID.fromString(userId), UUID.fromString(deviceId));
    }

    public void deleteAllUserSessions(String userId) {
        securityDeviceRepository.deleteAllByUserId(UUID.fromString(userId));
    }

    @Transactional
    public void deleteDeviceSession(UUID userId, UUID deviceId) {
        SecurityDevice deviceSession = securityDeviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new SecurityDeviceNotFoundException());

        if (!userId.equals(deviceSession.getUserId())) {
            throw new SecurityDeviceForbiddenException("Cannot delete session of other user");
        }

        this.securityDeviceRepository.deleteByDeviceId(deviceId);
    }
}
