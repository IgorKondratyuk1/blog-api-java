package org.development.blogApi.core.post.entity;

import jakarta.persistence.*;
import org.development.blogApi.core.blog.entity.Blog;
import org.development.blogApi.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

    @ManyToOne()
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @Column(name = "title")
    private String title;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "content")
    private String content;

    @OneToOne()
    @JoinColumn(name = "blog_id", referencedColumnName = "id")
    private Blog blog;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_banned")
    private boolean isBanned;

    // Constructor
    public Post(UUID id, UUID userId, Blog blog, String shortDescription, String content, String title, LocalDateTime createdAt, boolean isBanned) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.shortDescription = shortDescription;
        this.content = content;
        this.blog = blog;
        this.createdAt = createdAt;
        this.isBanned = isBanned;
    }

    public Post() {}

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    // Methods
    public void updatePost(UpdatePostOfBlogDto updatePostDto) {
        this.content = updatePostDto.getContent();
        this.title = updatePostDto.getTitle();
        this.shortDescription = updatePostDto.getShortDescription();
    }

//    public void updateBlog(Blog blog) {
//        this.blog = blog;
//    }
//
//    public void setIsBanned(boolean isBanned) {
//        this.isBanned = isBanned;
//    }

    public static Post createInstance(UUID userId, Blog blog, String shortDescription, String content, String title) {
        return new Post(
                UUID.randomUUID(),
                userId,
                blog,
                shortDescription,
                content,
                title,
                LocalDateTime.now(),
                false
        );
    }
}
