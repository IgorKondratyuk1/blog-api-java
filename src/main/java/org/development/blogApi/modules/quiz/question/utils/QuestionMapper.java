package org.development.blogApi.modules.quiz.question.utils;

import org.development.blogApi.modules.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;

public class QuestionMapper {
    public static ViewQuestionDto toView(QuizQuestionEntity quizQuestionEntity) {
        return new ViewQuestionDto(
                quizQuestionEntity.getId(),
                quizQuestionEntity.getBody(),
                quizQuestionEntity.getCorrectAnswers(),
                quizQuestionEntity.getPublished(),
                quizQuestionEntity.getCreatedAt(),
                quizQuestionEntity.getUpdatedAt()
        );
    }
}
