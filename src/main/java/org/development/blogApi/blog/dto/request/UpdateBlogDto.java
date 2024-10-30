package org.development.blogApi.blog.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateBlogDto {
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
}
