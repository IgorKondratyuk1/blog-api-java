package org.development.blogApi.like.dto.response;

import lombok.*;
import org.development.blogApi.like.enums.LikeStatus;


@Data
@AllArgsConstructor
public class LikeInfoDto {
    private long likesCount;
    private long dislikesCount;
    private LikeStatus myStatus;

    public LikeInfoDto() {
        this.myStatus = LikeStatus.NONE;
        this.likesCount = 0;
        this.dislikesCount = 0;
    }
}
