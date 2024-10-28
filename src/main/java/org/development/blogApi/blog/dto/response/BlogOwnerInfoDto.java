package org.development.blogApi.blog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlogOwnerInfoDto {
    private String userId;
    private String userLogin;
}
