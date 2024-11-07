package org.development.blogApi.quiz.question;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.quiz.question.dto.QuestionQueryParamsDto;
import org.development.blogApi.quiz.question.dto.request.CreateQuestionDto;
import org.development.blogApi.quiz.question.dto.response.ViewQuestionDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/quiz/questions")
@AllArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping("")
    public PaginationDto<ViewQuestionDto> findAllQuestions(QuestionQueryParamsDto questionQueryParamsDto) {
        return this.questionService.findAllQuestions();
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewQuestionDto createQuestion(@RequestBody @Valid CreateQuestionDto createQuestionDto) {
        return this.questionService.createQuestion(createQuestionDto);
    }
}
