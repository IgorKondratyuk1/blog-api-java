package org.development.blogApi.modules.quiz.question.repository;

import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.blogPlatform.core.blog.dto.response.ViewBlogDto;
import org.development.blogApi.modules.quiz.question.dto.QuestionQueryParamsDto;
import org.development.blogApi.modules.quiz.question.dto.response.ViewQuestionDto;

public interface QuestionQueryRepositoryCustom {
    // Method to find all blogs with pagination and filtering
    PaginationDto<ViewQuestionDto> findAllQuestions(QuestionQueryParamsDto queryObj);
}
