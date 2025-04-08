package org.development.blogApi.modules.blogPlatform.core.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikesDislikesCountDto {
    long likesCount;
    long dislikesCount;
}
