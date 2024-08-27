package org.development.blogApi.core.like.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.development.blogApi.core.like.enums.LikeStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment_like")
public class CommentLike extends Like {

    @Column(name = "comment_id")
    private UUID commentId;

    public CommentLike(UUID id, UUID userId, String userLogin, LikeStatus status, LocalDateTime createdAt, UUID commentId) {
        super(id, userId, userLogin, status, createdAt);
        this.commentId = commentId;
    }

    public CommentLike() {}

    public static Like createInstance(
            UUID userId,
            String userLogin,
            UUID locationId,
            LikeStatus status
    ) {
        return new CommentLike(
                UUID.randomUUID(),
                userId,
                userLogin,
                status,
                LocalDateTime.now(),
                locationId
        );
    }

    // Getters and setters

//    public UUID getCommentId() {
//        return commentId;
//    }
//
//    public void setCommentId(UUID commentId) {
//        this.commentId = commentId;
//    }

    @Override
    public UUID getLocationId() {
        return commentId;
    }

    @Override
    public void setLocationId(UUID locationId) {
        this.commentId = locationId;
    }
}
