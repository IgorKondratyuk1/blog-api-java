package org.development.blogApi.modules.blogPlatform.core.like.entity;

import jakarta.persistence.*;
import org.development.blogApi.modules.blogPlatform.core.comment.entity.Comment;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment_like")
public class CommentLike extends Like {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", updatable = false)
    private Comment comment;

    public CommentLike(UUID id, UUID userId, LikeStatus status, LocalDateTime createdAt, UUID commentId) {
        super(id, userId, status, createdAt);
        this.comment = new Comment();
        this.comment.setId(commentId);
    }

    public CommentLike() {}

    public static Like createInstance(
            UUID userId,
            UUID locationId,
            LikeStatus status
    ) {
        return new CommentLike(
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
        return this.comment.getId();
    }

    @Override
    public void setLocationId(UUID locationId) {
        comment.setId(locationId);
    }
}
