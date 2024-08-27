package org.development.blogApi.core.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateBlogDto {
    @NotBlank
    @Size(min = 1, max = 15)
    private String name;

    @NotEmpty
    @Size(min = 1, max = 100)
    @Pattern(regexp = "^(http://|https://).+$", message = "Must be a valid URL")
    private String websiteUrl;

    @NotEmpty
    @Size(min = 1, max = 500)
    private String description;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "CreateBlogDto{" +
                "name='" + name + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
