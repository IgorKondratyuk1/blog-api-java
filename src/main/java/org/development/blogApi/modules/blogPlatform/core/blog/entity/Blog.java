package org.development.blogApi.modules.blogPlatform.core.blog.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.modules.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne()
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "name")
    private String name;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "description")
    private String description;

    @Column(name = "is_membership")
    private boolean isMembership;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

//    private BanInfoEntity banInfo;

    public Blog(UUID id, UserEntity user, String name, String websiteUrl, String description, boolean isMembership, LocalDateTime createdAt) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.websiteUrl = websiteUrl;
        this.description = description;
        this.isMembership = isMembership;
        this.createdAt = createdAt;
    }

    public Blog() {
    }

    public static Blog createInstance(UserEntity user, CreateBlogDto createBlogDto) {
        return new Blog(
                UUID.randomUUID(),
                user,
                createBlogDto.getName(),
                createBlogDto.getWebsiteUrl(),
                createBlogDto.getDescription(),
                false,
                LocalDateTime.now()
        );
    }

    // Business methods
    public void updateBlog(UpdateBlogDto updateBlogDto) {
        this.name = updateBlogDto.getName();
        this.description = updateBlogDto.getDescription();
        this.websiteUrl = updateBlogDto.getWebsiteUrl();
    }

//    public void setIsBanned(boolean isBanned) {
//        this.banInfo.setBanned(isBanned);
//        this.banInfo.setBanDate(isBanned ? new Date() : null);
//    }

//    public UserEntity getUser() {
//        return user;
//    }
//
//    public void setUser(UserEntity user) {
//        this.user = user;
//    }

}
