package org.development.blogApi.like.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.development.blogApi.infrastructure.common.validation.enumCheck.EnumCheck;
import org.development.blogApi.like.enums.LikeStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateLikeDto {
    @NotNull(message = "Like status cannot be null")
    @EnumCheck(enumClass = LikeStatus.class, message = "Invalid like status")
    private String likeStatus;
}
