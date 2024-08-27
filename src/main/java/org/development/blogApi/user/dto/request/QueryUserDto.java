package org.development.blogApi.user.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.ToString;
import org.development.blogApi.common.dto.CommonQueryParamsDto;

@ToString
public class QueryUserDto extends CommonQueryParamsDto {
    @Size(max = 100, message = "Search login term can be up to 100 characters")
    private String searchLoginTerm = "";

    @Size(max = 100, message = "Search email term can be up to 100 characters")
    private String searchEmailTerm = "";

    @Pattern(regexp = "all|banned|notBanned", message = "Invalid ban status")
    private String banStatus = "all";

    // Constructors
    public QueryUserDto() {
        super();
    }

    public QueryUserDto(Integer pageNumber, Integer pageSize, String sortBy, String sortDirection, String searchLoginTerm, String searchEmailTerm, String banStatus) {
        super(null, pageNumber, pageSize, sortBy, sortDirection);
        this.searchLoginTerm = searchLoginTerm != null ? searchLoginTerm : "";
        this.searchEmailTerm = searchEmailTerm != null ? searchEmailTerm : "";
        this.banStatus = banStatus != null ? banStatus : "all";
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

    public String getBanStatus() {
        return banStatus;
    }

    public void setBanStatus(String banStatus) {
        this.banStatus = banStatus;
    }
}
