package org.development.blogApi.core.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.development.blogApi.core.like.dto.response.LikeInfoDto;
import org.development.blogApi.core.post.dto.response.ViewPostInfoDto;

@Data
@AllArgsConstructor
public class ViewBloggerCommentDto {
    private String id;
    private String content;
    private CommentatorInfoDto commentatorInfo;
    private String createdAt;
    private ViewPostInfoDto postInfo;
    private LikeInfoDto likeInfo;

    public ViewBloggerCommentDto(String id, String content, CommentatorInfoDto commentatorInfo, String createdAt, ViewPostInfoDto postInfo) {
        this.id = id;
        this.content = content;
        this.commentatorInfo = commentatorInfo;
        this.createdAt = createdAt;
        this.postInfo = postInfo;
        this.likeInfo = new LikeInfoDto();
    }
}
