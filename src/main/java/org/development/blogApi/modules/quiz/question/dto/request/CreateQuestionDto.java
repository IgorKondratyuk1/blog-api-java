package org.development.blogApi.modules.quiz.question.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateQuestionDto (
    @NotEmpty(message = "Body can not be empty")
    @Size(min = 10, max = 500, message = "Max body size is 1000 symbols")
    String body,

    @NotEmpty(message = "CorrectAnswers can not be empty")
    List<String> correctAnswers
) {}
