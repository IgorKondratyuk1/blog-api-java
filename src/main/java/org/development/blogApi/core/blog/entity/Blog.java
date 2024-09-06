package org.development.blogApi.core.blog.entity;

import jakarta.persistence.*;
import org.development.blogApi.core.blog.dto.request.CreateBlogDto;
import org.development.blogApi.core.blog.dto.request.UpdateBlogDto;
import org.development.blogApi.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "blog")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

//    @Column(name = "user_id")
//    private UUID userId;

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


    // Business methods
    public void updateBlog(UpdateBlogDto updateBlogDto) {
        this.name = updateBlogDto.getName();
        this.description = updateBlogDto.getDescription();
        this.websiteUrl = updateBlogDto.getWebsiteUrl();
    }

//    public void setOwner(UUID userId) {
//        this.userId = userId;
//    }

//    public void setIsBanned(boolean isBanned) {
//        this.banInfo.setBanned(isBanned);
//        this.banInfo.setBanDate(isBanned ? new Date() : null);
//    }

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

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isMembership() {
        return isMembership;
    }

    public void setMembership(boolean membership) {
        isMembership = membership;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

//    public UserEntity getUser() {
//        return user;
//    }
//
//    public void setUser(UserEntity user) {
//        this.user = user;
//    }

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", user=" + user +
                ", name='" + name + '\'' +
                ", websiteUrl='" + websiteUrl + '\'' +
                ", description='" + description + '\'' +
                ", isMembership=" + isMembership +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blog blog = (Blog) o;
        return isMembership == blog.isMembership && Objects.equals(id, blog.id) && Objects.equals(user, blog.user) && Objects.equals(name, blog.name) && Objects.equals(websiteUrl, blog.websiteUrl) && Objects.equals(description, blog.description) && Objects.equals(createdAt, blog.createdAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, name, websiteUrl, description, isMembership, createdAt);
    }
}
