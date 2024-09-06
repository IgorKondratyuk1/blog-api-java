package org.development.blogApi.core.comment.entity;

import jakarta.persistence.*;
import org.development.blogApi.core.comment.dto.request.CreateCommentDto;
import org.development.blogApi.core.post.entity.Post;
import org.development.blogApi.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;


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

    // Getters and setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }
//    public Blog getBlog() {
//        return blog;
//    }
//
//    public void setBlog(Blog blog) {
//        this.blog = blog;
//    }

//    public CommentatorInfo getCommentatorInfo() {
//        return commentatorInfo;
//    }
//
//    public void setCommentatorInfo(CommentatorInfo commentatorInfo) {
//        this.commentatorInfo = commentatorInfo;
//    }
//
//    public boolean isBanned() {
//        return isBanned;
//    }
//
//    public void setBanned(boolean banned) {
//        isBanned = banned;
//    }
}
