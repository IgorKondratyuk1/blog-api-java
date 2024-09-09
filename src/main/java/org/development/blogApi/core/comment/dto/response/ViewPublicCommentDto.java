package org.development.blogApi.core.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.development.blogApi.core.like.dto.response.LikeInfoDto;

@Data
@AllArgsConstructor
public class ViewPublicCommentDto {
    private String id;
    private String content;
    private CommentatorInfoDto commentatorInfo;
    private String createdAt;
    private LikeInfoDto likesInfo;
}
