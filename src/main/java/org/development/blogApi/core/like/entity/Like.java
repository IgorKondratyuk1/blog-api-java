package org.development.blogApi.core.like.entity;

import jakarta.persistence.*;
import org.development.blogApi.core.like.enums.LikeLocation;
import org.development.blogApi.core.like.enums.LikeStatus;
import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
public abstract class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "user_id")
    private UUID userId;

//    @Column(name = "user_login")
//    private String userLogin;

//    @Column(name = "id")
//    private String locationId;
//    private LikeLocation locationName;

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
            String userLogin,
//            String locationId,
//            LikeLocation locationName,
//            boolean isBanned,
            LikeStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
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
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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
