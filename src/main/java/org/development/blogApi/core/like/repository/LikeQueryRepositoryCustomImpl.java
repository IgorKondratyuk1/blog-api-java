package org.development.blogApi.core.like.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.development.blogApi.core.like.entity.Like;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class LikeQueryRepositoryCustomImpl implements LikeQueryRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Like> getLastLikesInfo(String locationId, LikeLocation locationName, int limitCount) {
        switch (locationName) {
            case COMMENT:
                return this.getLastCommentLikesInfo(locationId, limitCount);
            case POST:
                return this.getLastPostLikesInfo(locationId, limitCount);
            default:
                throw new RuntimeException("Type not found");
        }
    }

    @Override
    public List<Like> getLastCommentLikesInfo(String locationId, int limitCount) {
        String jpql = "SELECT cl FROM CommentLike cl " +
                        "WHERE cl.status = :status AND cl.comment.id = :locationId " +
                        "ORDER BY cl.createdAt DESC";

        return entityManager.createQuery(jpql, Like.class)
                .setParameter("status", LikeStatus.LIKE)
                .setParameter("locationId", UUID.fromString(locationId))
                .setMaxResults(limitCount)
                .getResultList();
    }

    @Override
    public List<Like> getLastPostLikesInfo(String locationId, int limitCount) {
        String selectLastPostLikes =
                "SELECT pl FROM PostLike pl " +
                        "WHERE pl.status = :status AND pl.post.id = :locationId " +
                        "ORDER BY pl.createdAt DESC";

        return entityManager.createQuery(selectLastPostLikes, Like.class)
                .setParameter("status", LikeStatus.LIKE)
                .setParameter("locationId", UUID.fromString(locationId))
                .setMaxResults(limitCount)
                .getResultList();
    }
}
