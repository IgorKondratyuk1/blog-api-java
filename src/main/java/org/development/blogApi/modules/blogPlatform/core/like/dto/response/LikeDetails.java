package org.development.blogApi.modules.blogPlatform.core.like.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikeDetails {
    private String addedAt;
    private String userId;
    private String login;
}
