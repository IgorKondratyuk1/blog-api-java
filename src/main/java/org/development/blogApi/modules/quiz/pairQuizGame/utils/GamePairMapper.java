package org.development.blogApi.modules.quiz.pairQuizGame.utils;

import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.*;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.AnswerEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePairEntity;
import org.development.blogApi.modules.quiz.pairQuizGame.entity.GamePlayerProgressEntity;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.development.blogApi.modules.user.entity.UserEntity;

import java.util.List;

public class GamePairMapper {

    public static ViewGamePairDto toView(GamePairEntity gamePairEntity) {
        ViewGamePlayerProgressDto viewFirstPlayerGamePlayerProgressDto = GamePairMapper.gamePlayerProgressEntityToView(gamePairEntity.getFirstPlayerProgress());
        ViewGamePlayerProgressDto viewSecondPlayerGamePlayerProgressDto = GamePairMapper.gamePlayerProgressEntityToView(gamePairEntity.getSecondPlayerProgress());
        List<ViewQuestionLightDto> viewQuestionLightDtos = gamePairEntity.getQuestions()
                .stream()
                .map(GamePairMapper::questionEntityToLightView)
                .toList();

        return new ViewGamePairDto(
                gamePairEntity.getId().toString(),
                viewFirstPlayerGamePlayerProgressDto,
                viewSecondPlayerGamePlayerProgressDto,
                viewQuestionLightDtos,
                gamePairEntity.getStatus(),
                gamePairEntity.getPairCreatedDate(),
                gamePairEntity.getStartGameDate(),
                gamePairEntity.getFinishGameDate()
        );
    }

    public static ViewAnswerDto answerEntityToView(AnswerEntity answer) {
        return new ViewAnswerDto(answer.getQuestion().getId().toString(), answer.getAnswerStatus(), answer.getAddedAt());
    }

    private static ViewQuestionLightDto questionEntityToLightView(QuizQuestionEntity quizQuestion) {
        return new ViewQuestionLightDto(quizQuestion.getId().toString(), quizQuestion.getBody());
    }

    private static ViewGamePlayerProgressDto gamePlayerProgressEntityToView(GamePlayerProgressEntity gamePlayerProgress) {
        List<ViewAnswerDto> viewFirstPlayerAnswerDtoList = gamePlayerProgress.getAnswers()
                .stream()
                .map(GamePairMapper::answerEntityToView)
                .toList();
        ViewPlayerDto viewFirstPlayerDto = GamePairMapper.userEntityToView(gamePlayerProgress.getPlayer());
        return new ViewGamePlayerProgressDto(viewFirstPlayerAnswerDtoList, viewFirstPlayerDto, gamePlayerProgress.getScore());
    }

    private static ViewPlayerDto userEntityToView(UserEntity user) {
        return new ViewPlayerDto(user.getId().toString(), user.getLogin());
    }
}
