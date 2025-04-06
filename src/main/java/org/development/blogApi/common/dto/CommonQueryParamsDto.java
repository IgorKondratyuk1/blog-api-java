package org.development.blogApi.common.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class CommonQueryParamsDto extends BasicQueryParamsDto {

    @Size(max = 255, message = "searchNameTerm term can be up to 255 characters")
    private String searchNameTerm = "";

    public CommonQueryParamsDto() {
        super();
    }

    public CommonQueryParamsDto(String searchNameTerm, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        super(pageNumber, pageSize, sortBy, sortDirection);
        this.searchNameTerm = searchNameTerm;
    }
}
