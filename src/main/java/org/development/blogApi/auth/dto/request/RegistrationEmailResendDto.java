package org.development.blogApi.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.development.blogApi.infrastructure.common.validation.isEmailNotConfirmed.IsEmailNotConfirmed;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationEmailResendDto {
    @NotEmpty(message = "Email should not be empty")
    @Length(min = 3, max = 200, message = "Email length must be between 3 and 200 characters")
    @Email(message = "Email should be valid")
    @IsEmailNotConfirmed(message = "Email is already confirmed")
    private String email;
}
