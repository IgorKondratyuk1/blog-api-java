package org.development.blogApi.modules.blogPlatform.core.like.dto.response;

public class LikesAndDislikesSummaryDto {
    private int likesCount;
    private int dislikesCount;

    public LikesAndDislikesSummaryDto(int likesCount, int dislikesCount) {
        this.likesCount = likesCount;
        this.dislikesCount = dislikesCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    @Override
    public String toString() {
        return "LikesDislikesCountDto{" +
                "likesCount=" + likesCount +
                ", dislikesCount=" + dislikesCount +
                '}';
    }
}
