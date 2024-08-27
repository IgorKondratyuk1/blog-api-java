package org.development.blogApi.core.like.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.development.blogApi.core.like.enums.LikeStatus;

public class UpdateLikeDto {
    @NotNull(message = "Like status cannot be null")
    @Pattern(regexp = "None|Like|Dislike", message = "Invalid like status") //TODO create own validator
    private LikeStatus likeStatus;

    public LikeStatus getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(LikeStatus likeStatus) {
        this.likeStatus = likeStatus;
    }
}
