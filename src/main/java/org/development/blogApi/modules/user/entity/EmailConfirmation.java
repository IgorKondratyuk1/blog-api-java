package org.development.blogApi.modules.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.development.blogApi.common.envHelpers.ApplicationEnvHelper;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
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


    public void setConfirmationCode(UUID confirmationCode) {
        if (this.isConfirmed()) {
            throw new IllegalStateException("Cannot set a new confirmation code if the code was confirmed");
        }

        this.confirmationCode = confirmationCode;
        this.expirationDate = generateNewExpirationDate();
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
