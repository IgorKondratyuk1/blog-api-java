package org.development.blogApi.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationConfirmationDto {
    // TODO Replace with your custom validation annotation if needed
    // @CodeResendRule
    @NotEmpty(message = "Code must not be empty")
    @Size(min = 1, message = "Code must be at least 1 character long")
    private String code;
}
