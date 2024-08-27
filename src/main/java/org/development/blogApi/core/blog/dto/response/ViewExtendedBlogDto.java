package org.development.blogApi.core.blog.dto.response;

import java.time.LocalDateTime;

public class ViewExtendedBlogDto {

    private String id;
    private String name;
    private String description;
    private String websiteUrl;
    private LocalDateTime createdAt;
    private boolean isMembership;
    private BlogOwnerInfoDto blogOwnerInfo;  // Use nullable reference (no need for Optional here)
//    private ViewBanInfoDto banInfo;

    // Constructor
    public ViewExtendedBlogDto(String id,
                               String name,
                               String description,
                               String websiteUrl,
                               LocalDateTime createdAt,
                               boolean isMembership,
                               BlogOwnerInfoDto blogOwnerInfo
//                               ViewBanInfoDto banInfo
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.websiteUrl = websiteUrl;
        this.createdAt = createdAt;
        this.isMembership = isMembership;
        this.blogOwnerInfo = blogOwnerInfo;
//        this.banInfo = banInfo;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isMembership() {
        return isMembership;
    }

    public void setMembership(boolean isMembership) {
        this.isMembership = isMembership;
    }

    public BlogOwnerInfoDto getBlogOwnerInfo() {
        return blogOwnerInfo;
    }

    public void setBlogOwnerInfo(BlogOwnerInfoDto blogOwnerInfo) {
        this.blogOwnerInfo = blogOwnerInfo;
    }

//    public ViewBanInfoDto getBanInfo() {
//        return banInfo;
//    }
//
//    public void setBanInfo(ViewBanInfoDto banInfo) {
//        this.banInfo = banInfo;
//    }
}

