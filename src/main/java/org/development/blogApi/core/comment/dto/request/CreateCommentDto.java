package org.development.blogApi.core.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;


public class CreateCommentDto {
    @NotEmpty(message = "Content must not be empty")
    @Size(min = 20, max = 300, message = "Content must be between 20 and 300 characters")
    private String content;

    // Getter and setter
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content.trim();
    }
}
