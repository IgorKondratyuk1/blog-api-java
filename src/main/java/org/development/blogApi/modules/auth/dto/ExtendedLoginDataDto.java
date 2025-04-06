package org.development.blogApi.modules.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtendedLoginDataDto {
    private String loginOrEmail;
    private String password;
    private String ip;
    private String title;
}
