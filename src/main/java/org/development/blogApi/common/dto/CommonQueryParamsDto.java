package org.development.blogApi.common.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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

    // Getters and Setters
    public String getSearchNameTerm() {
        return searchNameTerm;
    }

    public void setSearchNameTerm(String searchNameTerm) {
        this.searchNameTerm = searchNameTerm;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public String getSortDirection() {
        return sortDirection;
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection;
    }

    @Override
    public String toString() {
        return "CommonQueryParamsDto{" +
                "searchNameTerm='" + searchNameTerm + '\'' +
                ", pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", sortBy='" + sortBy + '\'' +
                ", sortDirection='" + sortDirection + '\'' +
                '}';
    }
}
