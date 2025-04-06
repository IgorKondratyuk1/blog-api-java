package org.development.blogApi.modules.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ViewUserDto {
    private String id;
    private String login;
    private String email;
    private LocalDateTime createdAt;
}
