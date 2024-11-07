package org.development.blogApi.blogPlatform.core.comment.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.development.blogApi.blogPlatform.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.blogPlatform.core.post.entity.Post;
import org.development.blogApi.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Entity
@Table(name = "\"comment\"")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "content")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "post_id", referencedColumnName = "id", updatable = false)
    private Post post;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    private UserEntity user;

//    @Column(name = "id")
//    private boolean isBanned;

    public Comment(UUID id, LocalDateTime createdAt, String content, UserEntity user, Post post) {
        this.id = id;
        this.createdAt = createdAt;
        this.content = content;
        this.user = user;
        this.post = post;
//        this.commentatorInfo = commentatorInfo;
//        this.isBanned = isBanned;
//        this.blog = blog;
    }

    public Comment() {}

    public static Comment createInstance(CreateCommentDto createCommentDto, UserEntity user, Post post) {
        return new Comment(
                UUID.randomUUID(),
                LocalDateTime.now(),
                createCommentDto.getContent(),
                user,
                post
        );
    }
}
