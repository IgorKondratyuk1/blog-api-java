package org.development.blogApi.modules.blogPlatform.core.like.entity;

import jakarta.persistence.*;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeLocation;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;
import org.development.blogApi.modules.user.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @OneToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private LikeStatus status;

//    @Column(name = "is_banned")
//    private boolean isBanned;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Like(
            UUID id,
            UUID userId,
//            String locationId,
//            LikeLocation locationName,
//            boolean isBanned,
            LikeStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.user = new UserEntity();
        user.setId(userId);
//        this.userLogin = userLogin;
//        this.locationId = locationId;
//        this.locationName = locationName;
//        this.isBanned = isBanned;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Like() {}

    public void setLikeStatus(LikeStatus likeStatus) {
        this.createdAt = LocalDateTime.now();
        this.status = likeStatus;
    }

    public static Like createInstance(
            String userId,
            String userLogin,
            LikeLocation locationName,
            String locationId,
            LikeStatus status
    ) {
        throw new UnsupportedOperationException("Must be implemented in a subclass");
//        return new LikeEntity(
//                UUID.randomUUID(),
//                UUID.fromString(userId),
//                userLogin,
////                locationId,
////                locationName,
////                false,
//                status,
//                LocalDateTime.now()
//        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return user.getId();
    }

    public void setUserId(UUID userId) {
        this.user.setId(userId);
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

//    public String getUserLogin() {
//        return userLogin;
//    }
//
//    public void setUserLogin(String userLogin) {
//        this.userLogin = userLogin;
//    }
//
//    public String getLocationId() {
//        return locationId;
//    }
//
//    public void setLocationId(String locationId) {
//        this.locationId = locationId;
//    }
//
//    public LikeLocation getLocationName() {
//        return locationName;
//    }
//
//    public void setLocationName(LikeLocation locationName) {
//        this.locationName = locationName;
//    }

    public LikeStatus getMyStatus() {
        return status;
    }

    public void setMyStatus(LikeStatus myStatus) {
        this.status = myStatus;
    }

//    public void setIsBanned(boolean banned) {
//        isBanned = banned;
//    }
//
//    public boolean getIsBanned() {
//        return isBanned;
//    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public abstract UUID getLocationId();

    public abstract void setLocationId(UUID locationId);
}
