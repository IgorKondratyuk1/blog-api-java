package org.development.blogApi.quiz.question.utils;

import org.development.blogApi.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.quiz.question.entity.Question;

public class QuestionMapper {
    public static ViewQuestionDto toView(Question question) {
        return new ViewQuestionDto(
                question.getId(),
                question.getBody(),
                question.getCorrectAnswers(),
                question.getPublished(),
                question.getCreatedAt(),
                question.getUpdatedAt()
        );
    }
}
