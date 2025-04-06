package org.development.blogApi.modules.auth.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.development.blogApi.common.dto.BasicQueryParamsDto;

@Data
@EqualsAndHashCode(callSuper = true)
public class QueryBannedUserDto extends BasicQueryParamsDto {

    @Size(max = 100, message = "Search login term can be up to 100 characters")
    private String searchLoginTerm = "";

    public QueryBannedUserDto() {
        super();
    }

    public QueryBannedUserDto(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String searchLoginTerm) {
        super(pageNumber, pageSize, sortBy, sortDirection);
        this.searchLoginTerm = searchLoginTerm != null ? searchLoginTerm : "";
    }
}
