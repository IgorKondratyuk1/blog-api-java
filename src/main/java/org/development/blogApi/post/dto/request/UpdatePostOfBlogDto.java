package org.development.blogApi.post.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;


@Data
@AllArgsConstructor
public class UpdatePostOfBlogDto {

    @NotEmpty
    @Size(min = 1, max = 30)
    private String title;

    @NotEmpty
    @Size(min = 1, max = 100)
    private String shortDescription;

    @NotEmpty
    @Size(min = 1, max = 1000)
    private String content;
}
