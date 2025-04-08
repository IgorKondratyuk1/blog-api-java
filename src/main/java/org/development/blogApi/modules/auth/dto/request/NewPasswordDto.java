package org.development.blogApi.modules.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NewPasswordDto {
    @NotEmpty
    @NotNull
    private String newPassword;

    @NotEmpty
    @NotNull
    private String recoveryCode;
}
