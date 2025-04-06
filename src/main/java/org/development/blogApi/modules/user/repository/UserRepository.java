package org.development.blogApi.modules.user.repository;

import org.development.blogApi.modules.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
    @Query("SELECT u FROM UserEntity u WHERE u.login = :loginOrEmail OR u.email = :loginOrEmail")
    Optional<UserEntity> findByLoginOrEmail(@Param("loginOrEmail") String loginOrEmail);


    @Query("SELECT u FROM UserEntity u WHERE u.emailConfirmation.confirmationCode = :confirmationCode")
    Optional<UserEntity> findByEmailConfirmationCode(@Param("confirmationCode") UUID confirmationCode);

    @Query("SELECT u FROM UserEntity u WHERE u.passwordRecovery.recoveryCode = :recoveryCode")
    Optional<UserEntity> findUserByPasswordConfirmationCode(@Param("recoveryCode") UUID recoveryCode);

    Boolean existsByLogin(String login);
}
