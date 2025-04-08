package org.development.blogApi.modules.blogPlatform.core.blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ViewExtendedBlogDto {
    private String id;
    private String name;
    private String description;
    private String websiteUrl;
    private LocalDateTime createdAt;
    @JsonProperty("isMembership")
    private boolean isMembership;
    private BlogOwnerInfoDto blogOwnerInfo;  // Use nullable reference (no need for Optional here)
//    private ViewBanInfoDto banInfo;
}

