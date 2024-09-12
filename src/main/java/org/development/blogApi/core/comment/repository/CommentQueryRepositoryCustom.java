package org.development.blogApi.core.comment.repository;

import org.development.blogApi.common.dto.CommonQueryParamsDto;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.core.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.core.comment.dto.response.ViewPublicCommentDto;

import java.util.Optional;
import java.util.UUID;

public interface CommentQueryRepositoryCustom {
    Optional<ViewPublicCommentDto> findCommentByIdAndUserId(UUID commentId, UUID currentUserId);

    PaginationDto<ViewPublicCommentDto> findCommentsOfPost(UUID postId, CommonQueryParamsDto queryObj, UUID currentUserId);

    PaginationDto<ViewBloggerCommentDto> findCommentsOfUserBlogs(UUID userId, CommonQueryParamsDto queryObj);
}
