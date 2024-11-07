package org.development.blogApi.blogPlatform.core.comment.repository;

import org.development.blogApi.blogPlatform.core.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.blogPlatform.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.infrastructure.common.dto.CommonQueryParamsDto;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;

import java.util.Optional;
import java.util.UUID;

public interface CommentQueryRepositoryCustom {
    Optional<ViewPublicCommentDto> findCommentByIdAndUserId(UUID commentId, UUID currentUserId);

    PaginationDto<ViewPublicCommentDto> findCommentsOfPost(UUID postId, CommonQueryParamsDto queryObj, UUID currentUserId);

    PaginationDto<ViewBloggerCommentDto> findCommentsOfUserBlogs(UUID userId, CommonQueryParamsDto queryObj);
}
