package org.development.blogApi.modules.blogPlatform.core.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.development.blogApi.modules.blogPlatform.core.like.dto.response.LikeInfoDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ViewPublicCommentDto {
    private String id;
    private String content;
    private CommentatorInfoDto commentatorInfo;
    private LocalDateTime createdAt;
    private LikeInfoDto likesInfo;
}
