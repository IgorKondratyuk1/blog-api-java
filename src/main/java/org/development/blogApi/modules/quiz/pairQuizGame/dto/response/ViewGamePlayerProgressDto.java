package org.development.blogApi.modules.quiz.pairQuizGame.dto.response;

import java.util.List;

public record ViewGamePlayerProgressDto (
    List<ViewAnswerDto> answers,
    ViewPlayerDto player,
    int score
) {}
