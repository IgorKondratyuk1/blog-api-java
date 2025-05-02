package org.development.blogApi.modules.quiz.pairQuizGame.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.util.List;

@Data
public class TopUsersQueryParams {

    @Min(1)
    private Integer pageNumber = 1;

    @Min(1)
    private Integer pageSize = 10;

    private List<String> sort = List.of("avgScores desc", "sort=sumScore desc");

    public TopUsersQueryParams(Integer pageNumber, Integer pageSize, List<String> sort) {
        this.pageNumber = pageNumber == null ? 1 : pageNumber;
        this.pageSize = pageSize == null ? 10 : pageSize;
        this.sort = sort == null ? List.of("avgScores desc", "sort=sumScore desc") : sort;
    }
}
