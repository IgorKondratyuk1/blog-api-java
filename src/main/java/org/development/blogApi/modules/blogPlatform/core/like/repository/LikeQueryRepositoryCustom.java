package org.development.blogApi.modules.blogPlatform.core.like.repository;

import org.development.blogApi.modules.blogPlatform.core.like.entity.Like;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeLocation;

import java.util.List;

public interface LikeQueryRepositoryCustom {
    public abstract List<Like> getLastLikesInfo(String locationId, LikeLocation locationName, int limitCount);

    public List<Like> getLastCommentLikesInfo(String locationId, int limitCount);

    public List<Like> getLastPostLikesInfo(String locationId, int limitCount);
}
