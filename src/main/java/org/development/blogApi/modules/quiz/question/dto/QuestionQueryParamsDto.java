package org.development.blogApi.modules.quiz.question.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.development.blogApi.common.dto.BasicQueryParamsDto;


@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionQueryParamsDto extends BasicQueryParamsDto {

    @Size(max = 255, message = "bodySearchTerm term can be up to 255 characters")
    private String bodySearchTerm = "";

    @Pattern(regexp = "all|published|notPublished")
    private String publishedStatus = "all";

    public QuestionQueryParamsDto() {
        super();
    }

    public QuestionQueryParamsDto(String bodySearchTerm, String publishedStatus, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        super(pageNumber, pageSize, sortBy, sortDirection);
        this.publishedStatus = publishedStatus;
        this.bodySearchTerm = bodySearchTerm;
    }
}
