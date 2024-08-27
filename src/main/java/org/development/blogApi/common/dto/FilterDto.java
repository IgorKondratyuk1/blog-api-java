package org.development.blogApi.common.dto;

import jakarta.validation.constraints.Size;


public class FilterDto {

    @Size(max = 100, message = "Search term can be up to 100 characters")
    private String searchNameTerm;

//    @NotNull(message = "Page number must not be null")
    private Integer pageNumber;

//    @NotNull(message = "Page size must not be null")
    private Integer pageSize;

//    @NotNull(message = "Sort by must not be null")
    private String sortBy;

//    @NotNull(message = "Sort direction must not be null")
    private String sortDirection;

    // Constructors
    public FilterDto(String searchNameTerm, Integer pageNumber, Integer pageSize, String sortBy, String sortDirection) {
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
}

