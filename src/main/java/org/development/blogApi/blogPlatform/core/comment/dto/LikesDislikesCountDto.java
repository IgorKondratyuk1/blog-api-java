package org.development.blogApi.blogPlatform.core.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LikesDislikesCountDto {
    long likesCount;
    long dislikesCount;
}
