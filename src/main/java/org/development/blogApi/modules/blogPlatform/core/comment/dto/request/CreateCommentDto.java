package org.development.blogApi.modules.blogPlatform.core.comment.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCommentDto {
    @NotEmpty(message = "Content must not be empty")
    @Size(min = 20, max = 300, message = "Content must be between 20 and 300 characters")
    private String content;
}
