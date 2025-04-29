package org.development.blogApi.modules.quiz.pairQuizGame.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.development.blogApi.common.dto.BasicQueryParamsDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class GamePairQueryParams extends BasicQueryParamsDto {

    public GamePairQueryParams() {
        super();
        this.setSortBy("pairCreatedDate");
    }

    public GamePairQueryParams(String searchNameTerm, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        super(pageNumber, pageSize, sortBy, sortDirection);
        if (sortBy.isEmpty()) {
            this.setSortBy("pairCreatedDate");
        }
    }
}
