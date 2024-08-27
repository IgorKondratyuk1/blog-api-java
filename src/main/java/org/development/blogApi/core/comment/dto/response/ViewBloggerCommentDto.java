package org.development.blogApi.core.comment.dto.response;

import org.development.blogApi.core.like.dto.response.LikeInfoDto;
import org.development.blogApi.core.post.dto.response.ViewPostInfoDto;

public class ViewBloggerCommentDto {
    private String id;
    private String content;
    private CommentatorInfoDto commentatorInfo;
    private String createdAt;
    private ViewPostInfoDto postInfo;
    private LikeInfoDto likeInfo;

    // Constructor
    public ViewBloggerCommentDto(String id, String content, CommentatorInfoDto commentatorInfo, String createdAt, ViewPostInfoDto postInfo) {
        this.id = id;
        this.content = content;
        this.commentatorInfo = commentatorInfo;
        this.createdAt = createdAt;
        this.postInfo = postInfo;
        this.likeInfo = new LikeInfoDto();
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

    public ViewPostInfoDto getPostInfo() {
        return postInfo;
    }

    public void setPostInfo(ViewPostInfoDto postInfo) {
        this.postInfo = postInfo;
    }

    public LikeInfoDto getLikesInfo() {
        return likeInfo;
    }

    public void setLikeInfo(LikeInfoDto likesInfo) {
        this.likeInfo = likesInfo;
    }
}
