package org.development.blogApi.core.like.dto.response;

import org.development.blogApi.core.like.enums.LikeStatus;

import java.util.ArrayList;
import java.util.List;

public class ExtendedLikeInfo extends LikeInfoDto {
    private List<LikeDetails> newestLikes;

    public ExtendedLikeInfo() {
        super();
        this.newestLikes = new ArrayList<>();
    }

    public ExtendedLikeInfo(long likesCount, long dislikesCount, LikeStatus myStatus, List<LikeDetails> newestLikes) {
        super(likesCount, dislikesCount, myStatus);
        this.newestLikes = newestLikes != null ? newestLikes : new ArrayList<>();
    }

    public List<LikeDetails> getNewestLikes() {
        return newestLikes;
    }

    public void setNewestLikes(List<LikeDetails> newestLikes) {
        this.newestLikes = newestLikes;
    }

    @Override
    public String toString() {
        return "ExtendedLikesInfo{" +
                "likesCount=" + getLikesCount() +
                ", dislikesCount=" + getDislikesCount() +
                ", myStatus=" + getMyStatus() +
                ", newestLikes=" + newestLikes +
                '}';
    }
}
