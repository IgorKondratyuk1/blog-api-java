package org.development.blogApi.core.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.development.blogApi.core.like.enums.LikeStatus;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_like")
public class PostLike extends Like {
    @Column(name = "post_id")
    private UUID postId;

    public PostLike(UUID id, UUID userId, String userLogin, LikeStatus status, LocalDateTime createdAt, UUID locationId) {
        super(id, userId, userLogin, status, createdAt);
        this.postId = locationId;
    }

    public PostLike() {}


    public static PostLike createInstance(
            UUID userId,
            String userLogin,
            UUID locationId,
            LikeStatus status
    ) {
        return new PostLike(
                UUID.randomUUID(),
                userId,
                userLogin,
                status,
                LocalDateTime.now(),
                locationId
        );
    }

    // Getters and setters
//    public UUID getPostId() {
//        return postId;
//    }
//
//    public void setPostId(UUID postId) {
//        this.postId = postId;
//    }

    @Override
    public UUID getLocationId() {
        return postId;
    }

    @Override
    public void setLocationId(UUID locationId) {
        this.postId = locationId;
    }
}
