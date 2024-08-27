package org.development.blogApi.core.blog.repository;

import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.core.blog.dto.response.ViewExtendedBlogDto;

import java.util.List;
import java.util.Optional;

public interface BlogQueryRepositoryCustom {
    // Method to find a blog by its ID
    Optional<ViewBlogDto> findOne(String id);

    // Method to find all blogs with pagination and filtering
    PaginationDto<ViewBlogDto> findAll(CommonQueryParamsDto queryObj, boolean skipBannedBlogs);

    // Method to find blogs with extended information
    PaginationDto<ViewExtendedBlogDto> findBlogsWithExtendedInfo(CommonQueryParamsDto queryObj);

    // Method to find blogs by a specific userId
    PaginationDto<ViewBlogDto> findBlogsByCreatedUserId(String userId, CommonQueryParamsDto queryObj);
}
