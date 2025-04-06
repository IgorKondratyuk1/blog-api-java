package org.development.blogApi.modules.securityDevice.repository;

import org.development.blogApi.modules.securityDevice.entity.SecurityDevice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SecurityDeviceRepository extends JpaRepository<SecurityDevice, UUID> {

    Optional<SecurityDevice> findByDeviceId(UUID deviceId);

    List<SecurityDevice> findAllByUserId(UUID userId);

    void deleteAllByUserId(UUID userId);

    void deleteByDeviceId(UUID deviceId);

    @Modifying
    @Query("DELETE FROM SecurityDevice sd WHERE sd.userId = :userId AND sd.deviceId != :currentSessionId")
    void deleteOtherSessionsExceptCurrent(UUID userId, UUID currentSessionId);
}
