package org.development.blogApi.modules.blogPlatform.core.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UpdatePostDto {

    @NotEmpty
    @Size(min = 1, max = 30)
    private String title;

    @NotEmpty
    @Size(min = 1, max = 100)
    private String shortDescription;

    @NotEmpty
    @Size(min = 1, max = 1000)
    private String content;

    @NotEmpty
    @Size(min = 1, max = 100)
    //@BlogExists TODO Custom annotation to validate if blog exists
    private String blogId;

    // Getters and Setters
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
}

