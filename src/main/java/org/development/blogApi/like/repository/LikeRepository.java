package org.development.blogApi.like.repository;

import org.development.blogApi.comment.dto.LikesDislikesCountDto;
import org.development.blogApi.like.entity.Like;
import org.development.blogApi.like.enums.LikeLocation;
import org.development.blogApi.like.enums.LikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

// TODO divide repositories for comment and post
@Repository
public interface LikeRepository extends JpaRepository<Like, UUID>, LikeQueryRepositoryCustom {

    default Optional<Like> getUserLike(UUID userId, UUID locationId, LikeLocation locationName) {
        switch (locationName) {
            case COMMENT:
                return this.getCommentUserLike(userId, locationId);
            case POST:
                return this.getPostUserLike(userId, locationId);
            default:
                return Optional.empty();
        }
    }

    default LikeStatus getUserLikeStatus(UUID userId, UUID locationId, LikeLocation locationName) {
        Optional<Like> likeOptional = this.getUserLike(userId, locationId, locationName);
        return likeOptional.isPresent() ? likeOptional.get().getMyStatus() : LikeStatus.NONE;
    }

    @Query("SELECT count(*) FROM CommentLike cl WHERE cl.comment.id = :commentId AND cl.status = :likeStatus")
    int getLikeOrDislikesCountOnComment(UUID commentId, LikeStatus likeStatus);

    @Query("SELECT count(*) FROM PostLike pl WHERE pl.post.id = :postId AND pl.status = :likeStatus")
    int getLikeOrDislikesCountOnPost(UUID postId, LikeStatus likeStatus);

    default int getLikeOrDislikesCount(UUID locationId, LikeLocation locationName, LikeStatus likeStatus) {
        switch (locationName) {
            case COMMENT:
                return this.getLikeOrDislikesCountOnComment(locationId, likeStatus);
            case POST:
                return this.getLikeOrDislikesCountOnPost(locationId, likeStatus);
            default:
                throw new RuntimeException("Wrong like location");
        }
    }

    default LikesDislikesCountDto getLikesAndDislikesCount(UUID locationId, LikeLocation locationName) {
        int likesCount = this.getLikeOrDislikesCount(locationId, locationName, LikeStatus.LIKE);
        int dislikesCount = this.getLikeOrDislikesCount(locationId, locationName, LikeStatus.DISLIKE);
        return new LikesDislikesCountDto(likesCount, dislikesCount);
    }

    @Query("SELECT u FROM CommentLike u WHERE u.user.id = :userId AND u.comment.id = :commentId")
    Optional<Like> getCommentUserLike(@Param("userId") UUID userId, @Param("commentId") UUID commentId);

    @Query("SELECT u FROM PostLike u WHERE u.user.id = :userId AND u.post.id = :postId")
    Optional<Like> getPostUserLike(@Param("userId") UUID userId, @Param("postId") UUID postId);

    @Modifying
    @Query("DELETE FROM PostLike u WHERE u.user.id = :userId AND u.post.id = :locationId")
    int deletePostLikeByUserIdAndLocationId(UUID userId, UUID locationId);

    @Modifying
    @Query("DELETE FROM CommentLike u WHERE u.user.id = :userId AND u.comment.id = :locationId")
    int deleteCommentLikeByUserIdAndLocationId(UUID userId, UUID locationId);

    @Modifying
    @Query("DELETE FROM CommentLike")
    void deleteAllCommentLikes();

    @Modifying
    @Query("DELETE FROM PostLike")
    void deleteAllPostLikes();
}
