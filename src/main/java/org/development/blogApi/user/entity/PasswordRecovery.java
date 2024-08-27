package org.development.blogApi.user.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_recovery")
public class PasswordRecovery {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "recoveryCode")
    private UUID recoveryCode;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "is_used")
    private boolean isUsed;

    // Constructor
    public PasswordRecovery(UUID recoveryCode, LocalDateTime expirationDate, boolean isUsed) {
        this.recoveryCode = recoveryCode;
        this.expirationDate = expirationDate;
        this.isUsed = isUsed;
    }

    public PasswordRecovery() {}

    // Getters and Setters
    public UUID getRecoveryCode() {
        return recoveryCode;
    }

    public void setRecoveryCode(UUID recoveryCode) {
        this.recoveryCode = recoveryCode;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    // Static factory method
    public static PasswordRecovery createInstance() {
        UUID recoveryCode = UUID.randomUUID(); // Assuming IdGenerator is a utility class
        return new PasswordRecovery(recoveryCode, generateNewExpirationDate(), false);
    }

    // Static method to generate a new expiration date
    public static LocalDateTime generateNewExpirationDate() {
        return LocalDateTime.now().plusHours(1); // TODO make env value
    }
}