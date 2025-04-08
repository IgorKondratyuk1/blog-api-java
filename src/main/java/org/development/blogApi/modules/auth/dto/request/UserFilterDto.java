package org.development.blogApi.modules.auth.dto.request;

import jakarta.validation.constraints.Size;
import org.development.blogApi.common.dto.FilterDto;

public class UserFilterDto extends FilterDto {
    @Size(max = 100, message = "Search login term can be up to 100 characters")
    private String searchLoginTerm;

    @Size(max = 200, message = "Search email term can be up to 200 characters")
    private String searchEmailTerm;

    public UserFilterDto(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String searchLoginTerm, String searchEmailTerm) {
        super(null, pageNumber, pageSize, sortBy, sortDirection);
        this.searchLoginTerm = searchLoginTerm;
        this.searchEmailTerm = searchEmailTerm;
    }

    // Getters and Setters
    public String getSearchLoginTerm() {
        return searchLoginTerm;
    }

    public void setSearchLoginTerm(String searchLoginTerm) {
        this.searchLoginTerm = searchLoginTerm;
    }

    public String getSearchEmailTerm() {
        return searchEmailTerm;
    }

    public void setSearchEmailTerm(String searchEmailTerm) {
        this.searchEmailTerm = searchEmailTerm;
    }
}
