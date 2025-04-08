package org.development.blogApi.modules.quiz.question.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateQuestionPublishStatusDto(
        @NotNull(message = "Published can not be empty")
        Boolean published
) {}
