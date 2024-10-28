package org.development.blogApi.blog.repository;

import org.development.blogApi.blog.dto.response.ViewBlogDto;
import org.development.blogApi.blog.dto.response.ViewExtendedBlogDto;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;

import java.util.Optional;
import java.util.UUID;

public interface BlogQueryRepositoryCustom {
    // Method to find a blog by its ID
    Optional<ViewBlogDto> findOneBlog(UUID id);

    // Method to find all blogs with pagination and filtering
    PaginationDto<ViewBlogDto> findAllBlogs(CommonQueryParamsDto queryObj, boolean skipBannedBlogs);

    // Method to find blogs with extended information
    PaginationDto<ViewExtendedBlogDto> findBlogsWithExtendedInfo(CommonQueryParamsDto queryObj);

    // Method to find blogs by a specific userId
    PaginationDto<ViewBlogDto> findBlogsByCreatedUserId(String userId, CommonQueryParamsDto queryObj);
}
