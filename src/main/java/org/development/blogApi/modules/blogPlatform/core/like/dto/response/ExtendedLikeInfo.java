package org.development.blogApi.modules.blogPlatform.core.like.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;

import java.util.ArrayList;
import java.util.List;


@Data
@EqualsAndHashCode(callSuper = true)
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
}
