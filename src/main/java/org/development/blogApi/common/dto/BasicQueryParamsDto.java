package org.development.blogApi.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicQueryParamsDto {
    @Min(1)
    private Integer pageNumber = 1;

    @Min(1)
    private Integer pageSize = 10;

    @Pattern(regexp = "^[a-zA-Z0-9_]*$")
    private String sortBy = "createdAt";

    @Pattern(regexp = "asc|desc")
    private String sortDirection = "desc";

    public BasicQueryParamsDto(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortDirection = sortDirection;
    }
}
