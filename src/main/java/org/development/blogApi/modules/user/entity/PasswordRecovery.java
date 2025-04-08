package org.development.blogApi.modules.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password_recovery")
public class PasswordRecovery {

//    @Id
//    @GeneratedValue(strategy = GenerationType.UUID)
//    @Column(name = "id")
//    private UUID id;

    @Id
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;

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