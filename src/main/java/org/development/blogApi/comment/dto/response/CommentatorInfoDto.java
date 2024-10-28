package org.development.blogApi.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentatorInfoDto {
    private String userId;
    private String userLogin;
}