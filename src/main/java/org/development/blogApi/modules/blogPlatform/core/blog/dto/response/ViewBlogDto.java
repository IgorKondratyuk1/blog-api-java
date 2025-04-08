package org.development.blogApi.modules.blogPlatform.core.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ViewBlogDto {
    private String id;
    private String name;
    private String description;
    private String websiteUrl;
    private LocalDateTime createdAt;
    @JsonProperty("isMembership")
    private boolean isMembership;
}
