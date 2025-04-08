package org.development.blogApi.modules.quiz.pairQuizGame.dto.response;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.AnswerStatus;

import java.time.LocalDateTime;

public record ViewAnswerDto(
        String questionId,
        AnswerStatus answerStatus,
        LocalDateTime addedAt
) {}
