package org.development.blogApi.auth.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDto { // TODO make DTOs without setters
    @NotEmpty(message = "loginOrEmail must not be empty")
    private String loginOrEmail;

    @NotEmpty(message = "password must not be empty")
    private String password;
}
