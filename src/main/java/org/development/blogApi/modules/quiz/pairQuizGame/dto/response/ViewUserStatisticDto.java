package org.development.blogApi.modules.quiz.pairQuizGame.dto.response;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.development.blogApi.modules.quiz.pairQuizGame.serializer.TwoDecimalDoubleSerializer;

public record ViewUserStatisticDto(
        int sumScore,

        @JsonSerialize(using = TwoDecimalDoubleSerializer.class)
        double avgScores,

        int gamesCount,

        int winsCount,

        int lossesCount,

        int drawsCount,

        ViewPlayerDto player
) {}

//@Data
//@AllArgsConstructor
//public class ViewUserStatisticDto {
//        int sumScore;
//
//        @JsonSerialize(using = TwoDecimalDoubleSerializer.class)
//        double avgScores;
//
//        int gamesCount;
//
//        int winsCount;
//
//        int lossesCount;
//
//        int drawsCount;
//
//        ViewPlayerDto player;
//}
