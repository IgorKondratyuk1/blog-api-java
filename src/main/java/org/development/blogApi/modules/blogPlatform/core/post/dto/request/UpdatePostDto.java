package org.development.blogApi.modules.blogPlatform.core.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record UpdatePostDto (

    @NotEmpty
    @Size(min = 1, max = 30)
    String title,

    @NotEmpty
    @Size(min = 1, max = 100)
    String shortDescription,

    @NotEmpty
    @Size(min = 1, max = 1000)
    String content,

    @NotEmpty
    @Size(min = 1, max = 100)
    //@BlogExists TODO Custom annotation to validate if blog exists
    String blogId
) {}

