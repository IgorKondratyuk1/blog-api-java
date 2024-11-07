package org.development.blogApi.blogPlatform.core.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class CreatePostDto {

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
}
