package org.development.blogApi.core.like.entity;

import jakarta.persistence.*;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.entity.Post;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post_like")
public class PostLike extends Like {

//    @Column(name = "post_id")
//    private UUID postId;

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
//    public UUID getPostId() {
//        return postId;
//    }
//
//    public void setPostId(UUID postId) {
//        this.postId = postId;
//    }

    @Override
    public UUID getLocationId() {
        return this.post.getId();
    }

    @Override
    public void setLocationId(UUID locationId) {
        this.post.setId(locationId);
    }
}
