package org.development.blogApi.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class UpdateUserDto {

    @NotEmpty(message = "Login must not be empty")
    @Length(min = 3, max = 10, message = "Login must be between 3 and 10 characters")
    private String login;

    @NotEmpty(message = "Password must not be empty")
    @Length(min = 6, max = 20, message = "Password must be between 6 and 20 characters")
    private String password;

    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    @Length(min = 3, max = 200, message = "Email must be between 3 and 200 characters")
    private String email;
}