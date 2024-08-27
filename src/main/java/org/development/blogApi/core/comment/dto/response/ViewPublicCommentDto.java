package org.development.blogApi.core.comment.dto.response;

import org.development.blogApi.core.like.dto.response.LikeInfoDto;

public class ViewPublicCommentDto {
    private String id;
    private String content;
    private CommentatorInfoDto commentatorInfo;
    private String createdAt;
    private LikeInfoDto likesInfo;

    // Constructor
    public ViewPublicCommentDto(String id, String content, CommentatorInfoDto commentatorInfo, String createdAt, LikeInfoDto likeInfoDto) {
        this.id = id;
        this.content = content;
        this.commentatorInfo = commentatorInfo;
        this.createdAt = createdAt;
        this.likesInfo = likeInfoDto;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public CommentatorInfoDto getCommentatorInfo() {
        return commentatorInfo;
    }

    public void setCommentatorInfo(CommentatorInfoDto commentatorInfo) {
        this.commentatorInfo = commentatorInfo;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public LikeInfoDto getLikesInfo() {
        return likesInfo;
    }

    public void setLikesInfo(LikeInfoDto likesInfo) {
        this.likesInfo = likesInfo;
    }
}
