package org.development.blogApi.modules.quiz.pairQuizGame.repository;

import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.GamePairQueryParams;
import org.development.blogApi.modules.quiz.pairQuizGame.dto.response.ViewGamePairDto;

public interface QuizGamePairQueryRepositoryCustom {

    PaginationDto<ViewGamePairDto> findAllGamePairs(GamePairQueryParams queryObj, String userId);
}
