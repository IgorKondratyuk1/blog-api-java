package org.development.blogApi.core.like.repository;

import org.development.blogApi.core.like.entity.Like;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.post.dto.response.ViewPostDto;

import java.util.List;
import java.util.Optional;

public interface LikeQueryRepositoryCustom {
    public abstract List<Like> getLastLikesInfo(String locationId, LikeLocation locationName, int limitCount);

    public List<Like> getLastCommentLikesInfo(String locationId, int limitCount);

    public List<Like> getLastPostLikesInfo(String locationId, int limitCount);
}
