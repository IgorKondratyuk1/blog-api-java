package org.development.blogApi.modules.blogPlatform.core.like.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.development.blogApi.modules.blogPlatform.core.like.entity.CommentLike;
import org.development.blogApi.modules.blogPlatform.core.like.entity.Like;
import org.development.blogApi.modules.blogPlatform.core.like.entity.PostLike;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeLocation;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;
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
                throw new RuntimeException("Type not found!");
        }
    }

    @Override
    public List<Like> getLastCommentLikesInfo(String locationId, int limitCount) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<CommentLike> criteriaQuery = criteriaBuilder.createQuery(CommentLike.class);
        Root<CommentLike> commentLikeRoot = criteriaQuery.from(CommentLike.class);

        Predicate statusPredicate = criteriaBuilder.equal(commentLikeRoot.get("comment").get("id"), UUID.fromString(locationId));
        Predicate commentIdPredicate = criteriaBuilder.equal(commentLikeRoot.get("status"), LikeStatus.LIKE);
        criteriaQuery.where(statusPredicate, commentIdPredicate);
        criteriaQuery.orderBy(criteriaBuilder.desc(commentLikeRoot.get("createdAt")));

        TypedQuery<CommentLike> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(limitCount);

        return List.copyOf(query.getResultList());
    }

    @Override
    public List<Like> getLastPostLikesInfo(String locationId, int limitCount) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<PostLike> criteriaQuery = criteriaBuilder.createQuery(PostLike.class);
        Root<PostLike> postLikeRoot = criteriaQuery.from(PostLike.class);

        Predicate statusPredicate = criteriaBuilder.equal(postLikeRoot.get("post").get("id"), UUID.fromString(locationId));
        Predicate commentIdPredicate = criteriaBuilder.equal(postLikeRoot.get("status"), LikeStatus.LIKE);
        criteriaQuery.where(statusPredicate, commentIdPredicate);
        criteriaQuery.orderBy(criteriaBuilder.desc(postLikeRoot.get("createdAt")));

        TypedQuery<PostLike> query = entityManager.createQuery(criteriaQuery);
        query.setMaxResults(limitCount);

        return List.copyOf(query.getResultList());
    }
}
