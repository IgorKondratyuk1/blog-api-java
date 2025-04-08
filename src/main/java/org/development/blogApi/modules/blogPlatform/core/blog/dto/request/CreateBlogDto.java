package org.development.blogApi.modules.blogPlatform.core.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBlogDto {
    @NotBlank
    @Size(max = 15)
    private String name;

    @NotEmpty
    @Size(max = 100)
    @Pattern(regexp = "^(http://|https://).+$", message = "Must be a valid URL")
    private String websiteUrl;

    @NotEmpty
    @Size(max = 500)
    private String description;
}
