package org.development.blogApi.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommonQueryParamsDto {
    @Size(max = 255)
    private String searchNameTerm = "";

    @Min(1)
    private Integer pageNumber = 1;

    @Min(1)
    private Integer pageSize = 10;

    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    private String sortBy = "createdAt";

    @Pattern(regexp = "asc|desc")
    private String sortDirection = "desc";

    public CommonQueryParamsDto() {}

    public CommonQueryParamsDto(String searchNameTerm, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        this.searchNameTerm = searchNameTerm;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
}
