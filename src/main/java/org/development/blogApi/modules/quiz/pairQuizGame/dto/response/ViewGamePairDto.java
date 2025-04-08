package org.development.blogApi.modules.quiz.pairQuizGame.dto.response;

import org.development.blogApi.modules.quiz.pairQuizGame.entity.enums.GamePairStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ViewGamePairDto (
        String id,
        ViewGamePlayerProgressDto firstPlayerProgress,
        ViewGamePlayerProgressDto secondPlayerProgress,
        List<ViewQuestionLightDto> questions,
        GamePairStatus status,
        LocalDateTime pairCreatedDate,
        LocalDateTime startGameDate,
        LocalDateTime finishGameDate
) {}
