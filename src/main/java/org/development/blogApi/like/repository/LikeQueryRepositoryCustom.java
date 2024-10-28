package org.development.blogApi.like.repository;

import org.development.blogApi.like.entity.Like;
import org.development.blogApi.like.enums.LikeLocation;

import java.util.List;

public interface LikeQueryRepositoryCustom {
    public abstract List<Like> getLastLikesInfo(String locationId, LikeLocation locationName, int limitCount);

    public List<Like> getLastCommentLikesInfo(String locationId, int limitCount);

    public List<Like> getLastPostLikesInfo(String locationId, int limitCount);
}
