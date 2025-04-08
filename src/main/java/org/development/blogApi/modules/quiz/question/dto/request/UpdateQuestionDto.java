package org.development.blogApi.modules.quiz.question.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record UpdateQuestionDto(
        @NotEmpty(message = "Body can not be empty")
        String body,

        @NotEmpty(message = "CorrectAnswers can not be empty")
        List<String> correctAnswers
) {}
