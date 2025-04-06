package org.development.blogApi.modules.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordRecoveryDto {
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Email should be valid")
    @Size(min = 3, max = 200, message = "Email should be between 3 and 200 characters")
    private String email;
}
