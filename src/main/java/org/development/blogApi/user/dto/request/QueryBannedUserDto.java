package org.development.blogApi.user.dto.request;

import jakarta.validation.constraints.Size;
import org.development.blogApi.common.dto.CommonQueryParamsDto;

public class QueryBannedUserDto extends CommonQueryParamsDto {

    @Size(max = 100, message = "Search login term can be up to 100 characters")
    private String searchLoginTerm = "";

    public QueryBannedUserDto() {
        super();
    }

    public QueryBannedUserDto(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String searchLoginTerm) {
        super(null, pageNumber, pageSize, sortBy, sortDirection);
        this.searchLoginTerm = searchLoginTerm != null ? searchLoginTerm : "";
    }

    // Getters and Setters
    public String getSearchLoginTerm() {
        return searchLoginTerm;
    }

    public void setSearchLoginTerm(String searchLoginTerm) {
        this.searchLoginTerm = searchLoginTerm;
    }
}
