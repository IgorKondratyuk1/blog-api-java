package org.development.blogApi.modules.quiz.pairQuizGame.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.development.blogApi.modules.quiz.pairQuizGame.serializer.TwoDecimalDoubleSerializer;

public record ViewMyStatisticDto(
        int sumScore,

        @JsonSerialize(using = TwoDecimalDoubleSerializer.class)
        double avgScores,

        int gamesCount,

        int winsCount,

        int lossesCount,

        int drawsCount
) {}
