package org.development.blogApi.modules.blogPlatform.core.post.repository;

import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.blogPlatform.core.post.dto.response.ViewPostDto;

import java.util.Optional;

public interface PostQueryRepositoryCustom {
    // Abstract method to find one post by ID
    public abstract Optional<ViewPostDto> findOnePost(String postId, String currentUserId);

    // Abstract method to find all posts based on the query object
    public abstract PaginationDto<ViewPostDto> findAllPosts(CommonQueryParamsDto queryObj, String currentUserId);

    // Abstract method to find posts of a specific blog
    public abstract PaginationDto<ViewPostDto> findPostsOfBlog(String blogId, CommonQueryParamsDto queryObj, String currentUserId);

    // Abstract method to find posts of a blog by the user's ID
    public abstract PaginationDto<ViewPostDto> findPostsOfBlogByUserId(String blogId, CommonQueryParamsDto queryObj, String userId);
}
