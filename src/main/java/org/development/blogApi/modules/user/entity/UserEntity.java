package org.development.blogApi.modules.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.development.blogApi.modules.auth.dto.request.RegistrationDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"user\"")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "login")
    private String login;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // TODO change fk to EmailConfirmation
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmailConfirmation emailConfirmation;

    // TODO change fk to PasswordRecovery
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private PasswordRecovery passwordRecovery;

    //    private SaUserBanInfo banInfo;

    // TODO clear data from user_roles table in testing/all-data route
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.DETACH})
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<RoleEntity> roles = new ArrayList<>();


    public UserEntity() {}

    public UserEntity(UUID id,
                      String login,
                      String email,
                      String passwordHash,
                      LocalDateTime createdAt,
                      EmailConfirmation emailConfirmation,
                      PasswordRecovery passwordRecovery,
                      List<RoleEntity> roles
                      //                SaUserBanInfo banInfo,
    ) {
        this.id = id;
        this.login = login;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
        this.emailConfirmation = emailConfirmation;
        this.passwordRecovery = passwordRecovery;
        this.roles = roles;
        //        this.banInfo = banInfo;
    }



    // Method to check if the email confirmation can be confirmed
    private boolean canBeConfirmed(UUID code) {
        System.out.println(this.emailConfirmation.getExpirationDate());
        System.out.println(LocalDateTime.now());
        return this.emailConfirmation.getConfirmationCode().equals(code) &&
                this.emailConfirmation.getExpirationDate().isAfter(LocalDateTime.now());
    }

    // Method to confirm email
    public void confirm(UUID code) {
        if (this.emailConfirmation.isConfirmed()) {
            throw new IllegalStateException("UserEntity is already confirmed");
        }
        if (!this.canBeConfirmed(code)) {
            throw new IllegalStateException("UserEntity can not be confirmed check ConfirmationCode or ExpirationDate");
        }

        this.emailConfirmation.setConfirmed(true);
    }

    // Method to set ban status
//    public void setIsBanned(boolean isBanned, String banReason) {
//        if (banReason == null || banReason.isEmpty()) {
//            throw new IllegalArgumentException("Cannot ban user without a ban reason");
//        }
//        this.banInfo.setBanned(isBanned);
//
//        if (isBanned) {
//            this.banInfo.setBanReason(banReason);
//            this.banInfo.setBanDate(LocalDateTime.now());
//        } else {
//            this.banInfo.setBanReason(null);
//            this.banInfo.setBanDate(null);
//        }
//    }


    // Method to create a new password recovery code
    public void createNewPasswordRecoveryCode() {
        UUID recoveryCode = UUID.randomUUID();
        PasswordRecovery passwordRecovery = new PasswordRecovery(recoveryCode, PasswordRecovery.generateNewExpirationDate(), false);
        passwordRecovery.setUser(this);
        this.passwordRecovery = passwordRecovery;
    }

    // Method to set a new password
    public void setPassword(String newPasswordHash) {
        if (this.passwordRecovery != null && this.passwordRecovery.isUsed()) {
            throw new IllegalStateException("Password recovery object is not created or already used");
        }

        if (this.passwordRecovery == null || this.passwordRecovery.getExpirationDate().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Password recovery date has expired or is not created");
        }

        this.setPasswordHash(newPasswordHash);
    }

    // Method to check if the password is correct
//    public boolean isPasswordCorrect(String password) {
//        return BCryptUtils.matches(password, this.accountData.getPasswordHash());
//    }

    // Static method to generate password hash
//    private static String generatePasswordHash(String password) {
//        return BCryptUtils.hash(password); // Assumes BCryptUtils is a utility class for BCrypt operations
//    }

    // Static factory method
    public static UserEntity createInstance(RegistrationDto createUserDto, String passwordHash, List<RoleEntity> roles) {
        return UserEntity.createInstance(createUserDto, passwordHash, roles, false);
    }

    // For SA user creation
    public static UserEntity createInstance(RegistrationDto createUserDto, String passwordHash, List<RoleEntity> roles, boolean isConfirmed) {
        UUID userId = UUID.randomUUID();
        EmailConfirmation emailConfirmation = EmailConfirmation.createInstance(isConfirmed);

        UserEntity user = new UserEntity(
                userId,
                createUserDto.getLogin(),
                createUserDto.getEmail(),
                passwordHash,
                LocalDateTime.now(),
                emailConfirmation,
                null,
                roles
        );
        emailConfirmation.setUser(user);
        //        SaUserBanInfo banInfo = new SaUserBanInfo(false, null, null);

        return user;
    }
}
