package org.development.blogApi.core.like.dto.response;

import org.development.blogApi.core.like.enums.LikeStatus;

public class LikeInfoDto {
    private long likesCount = 0;
    private long dislikesCount = 0;
    private LikeStatus myStatus = LikeStatus.NONE;

    public LikeInfoDto() {}

    public LikeInfoDto(long likesCount, long dislikesCount, LikeStatus myStatus) {
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
        this.myStatus = myStatus;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public long getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public LikeStatus getMyStatus() {
        return myStatus;
    }

    public void setMyStatus(LikeStatus myStatus) {
        this.myStatus = myStatus;
    }

    @Override
    public String toString() {
        return "LikesInfoDto{" +
                "likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                ", myStatus=" + myStatus +
                '}';
    }
}
