package org.development.blogApi.core.like.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class CreateLikeDto {
    @NotNull(message = "Like status cannot be null")
    @Pattern(regexp = "None|Like|Dislike", message = "Invalid like status") //TODO create own validator
    private String likeStatus;

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }
}
