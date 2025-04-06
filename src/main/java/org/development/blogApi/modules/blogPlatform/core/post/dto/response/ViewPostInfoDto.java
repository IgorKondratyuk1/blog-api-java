package org.development.blogApi.modules.blogPlatform.core.post.dto.response;

public class ViewPostInfoDto {

    private String id;
    private String title;
    private String blogId;
    private String blogName;

    // Constructor
    public ViewPostInfoDto(String id, String title, String blogId, String blogName) {
        this.id = id;
        this.title = title;
        this.blogId = blogId;
        this.blogName = blogName;
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
}

