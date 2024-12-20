package org.development.blogApi.user.entity;

import jakarta.persistence.*;
import org.development.blogApi.infrastructure.common.envHelpers.ApplicationEnvHelper;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_confirmation")
public class EmailConfirmation {

    @Id
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

    @Column(name = "confirmation_code")
    private UUID confirmationCode;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;

    @Column(name = "is_confirmed")
    private boolean isConfirmed;


    public EmailConfirmation(UUID confirmationCode, LocalDateTime expirationDate, boolean isConfirmed) {
        this.confirmationCode = confirmationCode;
        this.expirationDate = expirationDate;
        this.isConfirmed = isConfirmed;
    }

    public EmailConfirmation() {}

    // Getters and Setters
    public UUID getConfirmationCode() {
        return confirmationCode;
    }

    public void setConfirmationCode(UUID confirmationCode) {
        if (this.isConfirmed()) {
            throw new IllegalStateException("Cannot set a new confirmation code if the code was confirmed");
        }

        this.confirmationCode = confirmationCode;
        this.expirationDate = generateNewExpirationDate();
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isConfirmed() {
        return isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        isConfirmed = confirmed;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    // Static factory method
    public static EmailConfirmation createInstance() {
        return EmailConfirmation.createInstance(false);
    }

    // For SA
    public static EmailConfirmation createInstance(boolean isConfirmed) {
        UUID confirmationCode = UUID.randomUUID();
        return new EmailConfirmation(confirmationCode, generateNewExpirationDate(), isConfirmed);
    }

    // Static method to generate a new expiration date
    private static LocalDateTime generateNewExpirationDate() {
        int emailConfirmationExpirationHours = ApplicationEnvHelper.getEmailConfirmationExpirationHours();
        return LocalDateTime.now().plusHours(emailConfirmationExpirationHours);
    }
}
