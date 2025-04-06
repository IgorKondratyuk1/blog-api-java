package org.development.blogApi.modules.blogPlatform.core.post.dto.response;

import org.development.blogApi.modules.blogPlatform.core.like.dto.response.ExtendedLikeInfo;

import java.time.LocalDateTime;

public class ViewPostDto {

    private String id;
    private String title;
    private String shortDescription;
    private String content;
    private String blogId;
    private String blogName;
    private LocalDateTime createdAt;
    private ExtendedLikeInfo extendedLikesInfo;

    // Constructor
    public ViewPostDto(String id, String title, String shortDescription, String content, String blogId, String blogName, LocalDateTime createdAt, ExtendedLikeInfo extendedLikesInfo) {
        this.id = id;
        this.title = title;
        this.shortDescription = shortDescription;
        this.content = content;
        this.blogId = blogId;
        this.blogName = blogName;
        this.createdAt = createdAt;
        this.extendedLikesInfo = extendedLikesInfo != null ? extendedLikesInfo : new ExtendedLikeInfo();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBlogId() {
        return blogId;
    }

    public void setBlogId(String blogId) {
        this.blogId = blogId;
    }

    public String getBlogName() {
        return blogName;
    }

    public void setBlogName(String blogName) {
        this.blogName = blogName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ExtendedLikeInfo getExtendedLikesInfo() {
        return extendedLikesInfo;
    }

    public void setExtendedLikesInfo(ExtendedLikeInfo extendedLikesInfo) {
        this.extendedLikesInfo = extendedLikesInfo;
    }
}

