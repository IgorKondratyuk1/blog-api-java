package org.development.blogApi.blogPlatform.core.like.entity;

import jakarta.persistence.*;
import org.development.blogApi.blogPlatform.core.like.enums.LikeStatus;
import org.development.blogApi.blogPlatform.core.post.entity.Post;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_like")
public class PostLike extends Like {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", updatable = false)
    private Post post;

    public PostLike(UUID id, UUID userId, LikeStatus status, LocalDateTime createdAt, UUID locationId) {
        super(id, userId, status, createdAt);
        this.post = new Post();
        this.post.setId(locationId);
    }

    public PostLike() {}


    public static PostLike createInstance(
            UUID userId,
            UUID locationId,
            LikeStatus status
    ) {
        return new PostLike(
                UUID.randomUUID(),
                userId,
                status,
                LocalDateTime.now(),
                locationId
        );
    }

    // Getters and setters
    @Override
    public UUID getLocationId() {
        return this.post.getId();
    }

    @Override
    public void setLocationId(UUID locationId) {
        this.post.setId(locationId);
    }
}
