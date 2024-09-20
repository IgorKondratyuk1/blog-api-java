package org.development.blogApi.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.development.blogApi.common.validation.codeNotUsed.CodeNotUsed;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationConfirmationDto {
    @NotEmpty(message = "Code must not be empty")
    @Size(min = 1, message = "Code must be at least 1 character long")
    @CodeNotUsed(message = "Invalid code or code is already used")
    private String code;
}
