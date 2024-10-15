package org.development.blogApi.core.like.entity;

import jakarta.persistence.*;
import org.development.blogApi.core.comment.entity.Comment;
import org.development.blogApi.core.like.enums.LikeStatus;
import org.development.blogApi.core.post.entity.Post;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "comment_like")
public class CommentLike extends Like {

//    @Column(name = "comment_id")
//    private UUID commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", updatable = false)
    private Comment comment;

    public CommentLike(UUID id, UUID userId, LikeStatus status, LocalDateTime createdAt, UUID commentId) {
        super(id, userId, status, createdAt);
//        this.commentId = commentId;
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

//    public UUID getCommentId() {
//        return commentId;
//    }
//
//    public void setCommentId(UUID commentId) {
//        this.commentId = commentId;
//    }

    @Override
    public UUID getLocationId() {
        return this.comment.getId();
    }

    @Override
    public void setLocationId(UUID locationId) {
        comment.setId(locationId);
    }
}
