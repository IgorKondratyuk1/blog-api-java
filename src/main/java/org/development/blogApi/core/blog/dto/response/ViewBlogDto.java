package org.development.blogApi.core.blog.dto.response;

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
    private boolean isMembership;
}
