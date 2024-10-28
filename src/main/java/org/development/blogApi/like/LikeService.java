package org.development.blogApi.like;

import org.development.blogApi.like.entity.CommentLike;
import org.development.blogApi.like.entity.Like;
import org.development.blogApi.like.entity.PostLike;
import org.development.blogApi.like.enums.LikeLocation;
import org.development.blogApi.like.enums.LikeStatus;
import org.development.blogApi.like.repository.LikeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public void like(String userId, String userLogin, LikeLocation locationName, String locationId, LikeStatus status) {
        switch (status) {
            case LIKE:
            case DISLIKE:
                this.createOrUpdateLike(userId, userLogin, locationName, locationId, status);
                break;
            case NONE:
                this.removeLike(userId, locationName, locationId);
                break;
            default:
                throw new RuntimeException("Wrong like status: " + status);
        }
    }

    public void createOrUpdateLike(String userId, String userLogin, LikeLocation locationName, String locationId, LikeStatus status) {
        Optional<Like> optionalExistingLike = this.likeRepository.getUserLike(UUID.fromString(userId), UUID.fromString(locationId), locationName);

        if (optionalExistingLike.isPresent()) {
            Like existingLike = optionalExistingLike.get();
            existingLike.setLikeStatus(status);
            likeRepository.save(existingLike);
            return;
        }

        Like newLike;
        switch (locationName) {
            case POST:
                newLike = PostLike.createInstance(UUID.fromString(userId), UUID.fromString(locationId), status);
                break;
            case COMMENT:
                newLike = CommentLike.createInstance(UUID.fromString(userId), UUID.fromString(locationId), status);
                break;
            default:
                throw new RuntimeException("No location founded");
        }

        this.likeRepository.save(newLike);
    }

    @Transactional
    public void updateLike(Like like) {
        Like existingLikeEntity = this.likeRepository.findById(like.getId()).orElseThrow(() -> new RuntimeException("Like not found"));

        existingLikeEntity.setLikeStatus(like.getMyStatus());
        existingLikeEntity.setCreatedAt(like.getCreatedAt());
        existingLikeEntity.setLocationId(like.getLocationId());
        existingLikeEntity.setUserId(like.getUserId());
        this.likeRepository.save(existingLikeEntity);
    }

    public void removeLike(String userId, LikeLocation locationName, String locationId) {
        switch (locationName) {
            case POST:
                this.likeRepository.deletePostLikeByUserIdAndLocationId(UUID.fromString(userId), UUID.fromString(locationId));
                break;
            case COMMENT:
                this.likeRepository.deleteCommentLikeByUserIdAndLocationId(UUID.fromString(userId), UUID.fromString(locationId));
                break;
            default:
                throw new RuntimeException("Wrong locationName");
        }
    }

    public void removeAll() {
        this.likeRepository.deleteAll();
    }

//    public boolean setBanStatusToUserLikes(String userId, boolean isBanned) {
//        try {
//            // Fetch and update likes based on userId
//            List<Like> likes = likeRepository.findAllByUserId(userId);
//            likes.forEach(like -> {
//                like.setBanned(isBanned);
//                likeRepository.save(like);
//            });
//            return true;
//        } catch (Exception e) {
//            System.err.println(e.getMessage());
//            return false;
//        }
//    }

}
