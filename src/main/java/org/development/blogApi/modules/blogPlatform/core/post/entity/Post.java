package org.development.blogApi.modules.blogPlatform.core.post.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.development.blogApi.modules.blogPlatform.core.blog.entity.Blog;
import org.development.blogApi.modules.blogPlatform.core.post.dto.request.UpdatePostOfBlogDto;
import org.development.blogApi.modules.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "post")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "user_id", updatable = false)
    private UserEntity user;

    @Column(name = "title")
    private String title;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "content")
    private String content;

    @ManyToOne()
    @JoinColumn(name = "blog_id", referencedColumnName = "id", updatable = false)
    private Blog blog;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "is_banned")
    private boolean isBanned;

    // Constructor
    public Post(UUID id, UserEntity user, Blog blog, String shortDescription, String content, String title, LocalDateTime createdAt, boolean isBanned) {
        this.id = id;
        this.user = user;
        this.title = title;
        this.shortDescription = shortDescription;
        this.content = content;
        this.blog = blog;
        this.createdAt = createdAt;
        this.isBanned = isBanned;
    }

    public Post() {}

    // Methods
    public void updatePost(UpdatePostOfBlogDto updatePostDto) {
        this.content = updatePostDto.getContent();
        this.title = updatePostDto.getTitle();
        this.shortDescription = updatePostDto.getShortDescription();
    }

    public static Post createInstance(UserEntity user, Blog blog, String shortDescription, String content, String title) {
        return new Post(
                UUID.randomUUID(),
                user,
                blog,
                shortDescription,
                content,
                title,
                LocalDateTime.now(),
                false
        );
    }
}
