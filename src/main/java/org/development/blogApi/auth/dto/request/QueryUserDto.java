package org.development.blogApi.auth.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;

@Data
public class QueryUserDto extends CommonQueryParamsDto {
    @Size(max = 100, message = "Search login term can be up to 100 characters")
    private String searchLoginTerm = "";

    @Size(max = 100, message = "Search email term can be up to 100 characters")
    private String searchEmailTerm = "";

    @Pattern(regexp = "all|banned|notBanned", message = "Invalid ban status")
    private String banStatus = "all";

    public QueryUserDto() {
        super();
    }

    public QueryUserDto(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String searchLoginTerm, String searchEmailTerm, String banStatus) {
        super(null, pageNumber, pageSize, sortBy, sortDirection);
        this.searchLoginTerm = searchLoginTerm != null ? searchLoginTerm : "";
        this.searchEmailTerm = searchEmailTerm != null ? searchEmailTerm : "";
        this.banStatus = banStatus != null ? banStatus : "all";
    }
}
