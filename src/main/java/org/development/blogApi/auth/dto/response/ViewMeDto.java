package org.development.blogApi.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ViewMeDto {
    private String userId;
    private String login;
    private String email;
}
