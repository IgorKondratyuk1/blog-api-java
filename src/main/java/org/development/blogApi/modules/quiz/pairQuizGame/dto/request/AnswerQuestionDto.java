package org.development.blogApi.modules.quiz.pairQuizGame.dto.request;

import jakarta.validation.constraints.NotNull;

public record AnswerQuestionDto(
        @NotNull
        String answer
) {}
