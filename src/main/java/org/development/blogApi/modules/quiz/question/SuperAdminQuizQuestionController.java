package org.development.blogApi.modules.quiz.question;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.development.blogApi.common.dto.PaginationDto;
import org.development.blogApi.modules.quiz.question.dto.QuestionQueryParamsDto;
import org.development.blogApi.modules.quiz.question.dto.request.CreateQuestionDto;
import org.development.blogApi.modules.quiz.question.dto.request.UpdateQuestionDto;
import org.development.blogApi.modules.quiz.question.dto.request.UpdateQuestionPublishStatusDto;
import org.development.blogApi.modules.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.modules.quiz.question.repository.QuizQuestionQueryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/sa/quiz/questions")
@AllArgsConstructor
public class SuperAdminQuizQuestionController {

    private final QuizQuestionService quizQuestionService;

    private final QuizQuestionQueryRepository quizQuestionQueryRepository;

    @GetMapping("")
    public PaginationDto<ViewQuestionDto> findAllQuestions(QuestionQueryParamsDto questionQueryParamsDto) {
        return this.quizQuestionQueryRepository.findAllQuestions(questionQueryParamsDto);
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public ViewQuestionDto createQuestion(@RequestBody @Valid CreateQuestionDto createQuestionDto) {
        return this.quizQuestionService.createQuestion(createQuestionDto);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuestion(@PathVariable String id,
                               @RequestBody @Valid UpdateQuestionDto updateQuestionDto) {
        this.quizQuestionService.updateQuestion(id, updateQuestionDto);
    }

    @PutMapping("/{id}/publish")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateQuestionPublishStatus(@PathVariable String id,
                                            @RequestBody @Valid UpdateQuestionPublishStatusDto updateQuestionPublishStatusDto
    ) {
        this.quizQuestionService.updateQuestionPublishStatus(id, updateQuestionPublishStatusDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteQuestion(@PathVariable String id) {
        this.quizQuestionService.deleteQuestionById(id);
    }
}
