package org.development.blogApi.post.utils;


import org.development.blogApi.like.dto.response.ExtendedLikeInfo;
import org.development.blogApi.like.dto.response.LikeDetails;
import org.development.blogApi.like.entity.Like;
import org.development.blogApi.like.enums.LikeStatus;
import org.development.blogApi.post.dto.response.ViewPostDto;
import org.development.blogApi.post.entity.Post;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostMapper {

    // Converts a LikeEntity or LikeMongoEntity to LikeDetails
    public static LikeDetails toLikeDetails(Like likeEntity) {
        return new LikeDetails(
                likeEntity.getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME),
                likeEntity.getUserId().toString(),
                likeEntity.getUser().getLogin()
        );
    }

    // Converts a PostMongoEntity or PostEntity to ViewPostDto

    public static ViewPostDto toView(Post post) {
        return PostMapper.toView(post, LikeStatus.NONE, 0, 0, new ArrayList<Like>());
    }

    public static ViewPostDto toView(
            Post post,
            LikeStatus likeStatus,
            long likesCount,
            long dislikesCount,
            List<Like> lastLikeEntities) {

        List<LikeDetails> newestLikes = lastLikeEntities.stream()
                .map(PostMapper::toLikeDetails)
                .collect(Collectors.toList());

        ExtendedLikeInfo extendedLikesInfo = new ExtendedLikeInfo(
                likesCount,
                dislikesCount,
                likeStatus,
                newestLikes
        );

        return new ViewPostDto(
                post.getId().toString(),
                post.getTitle(),
                post.getShortDescription(),
                post.getContent(),
                post.getBlog().getId().toString(),
                post.getBlog().getName(),
                post.getCreatedAt(),
                extendedLikesInfo
        );
    }
}

