package org.development.blogApi.modules.blogPlatform.core.comment.utils;

import org.development.blogApi.modules.blogPlatform.core.comment.dto.response.CommentatorInfoDto;
import org.development.blogApi.modules.blogPlatform.core.comment.dto.response.ViewBloggerCommentDto;
import org.development.blogApi.modules.blogPlatform.core.comment.dto.response.ViewPublicCommentDto;
import org.development.blogApi.modules.blogPlatform.core.comment.entity.Comment;
import org.development.blogApi.modules.blogPlatform.core.like.dto.response.LikeInfoDto;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;
import org.development.blogApi.modules.blogPlatform.core.post.dto.response.ViewPostInfoDto;
import org.development.blogApi.modules.blogPlatform.core.post.entity.Post;

public class CommentMapper {

    public static ViewPublicCommentDto toPublicViewFromDomain(Comment comment) {
        return CommentMapper.toPublicViewFromDomain(comment, LikeStatus.NONE, 0, 0);
    }

    public static ViewPublicCommentDto toPublicViewFromDomain(
            Comment comment,
            LikeStatus likeStatus,
            long likesCount,
            long dislikesCount
            )
    {
        LikeInfoDto likeInfo = new LikeInfoDto(likesCount, dislikesCount, likeStatus);

        return new ViewPublicCommentDto(
                comment.getId().toString(),
                comment.getContent(),
                new CommentatorInfoDto(comment.getUser().getId().toString(), comment.getUser().getLogin()),
                comment.getCreatedAt(),
                likeInfo);
    }

    public static ViewBloggerCommentDto toBloggerView(
            Comment comment,
            Post post,
            LikeStatus likeStatus,
            long likesCount,
            long dislikesCount
    )
    {
        ViewPostInfoDto viewPostInfoDto = new ViewPostInfoDto(
                post.getId().toString(),
                post.getTitle(),
                post.getBlog().getId().toString(),
                post.getBlog().getName()
        );

        LikeInfoDto likeInfoDto = new LikeInfoDto(likesCount, dislikesCount, likeStatus);

        return new ViewBloggerCommentDto(
                comment.getId().toString(),
                comment.getContent(),
                new CommentatorInfoDto(comment.getUser().getId().toString(), comment.getUser().getLogin()),
                comment.getCreatedAt(),
                viewPostInfoDto,
                likeInfoDto
        );
    }

    public static ViewBloggerCommentDto toBloggerView(Comment comment, Post post) {
        return CommentMapper.toBloggerView(comment, post, LikeStatus.NONE, 0, 0);
    }
}
