package org.development.blogApi.core.like.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.development.blogApi.common.validation.enumCheck.EnumCheck;
import org.development.blogApi.core.like.enums.LikeStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateLikeDto {
    @NotNull(message = "Like status cannot be null")
    @EnumCheck(enumClass = LikeStatus.class, message = "Invalid like status")
    private String likeStatus;
}
