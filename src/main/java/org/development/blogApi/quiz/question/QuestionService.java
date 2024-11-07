package org.development.blogApi.quiz.question;

import lombok.AllArgsConstructor;
import org.development.blogApi.infrastructure.common.dto.PaginationDto;
import org.development.blogApi.quiz.question.dto.request.CreateQuestionDto;
import org.development.blogApi.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.quiz.question.entity.Question;
import org.development.blogApi.quiz.question.repository.QuestionRepository;
import org.development.blogApi.quiz.question.utils.QuestionMapper;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;

    public PaginationDto<ViewQuestionDto> findAllQuestions() {
        List<Question> questionList = this.questionRepository.findAll();
        List<ViewQuestionDto> viewQuestionList = questionRepository.findAll().stream().map((Question q) -> QuestionMapper.toView(q)).collect(Collectors.toList());
        System.out.println(Arrays.toString(viewQuestionList.toArray()));
        return null;
    }

    public ViewQuestionDto createQuestion(CreateQuestionDto createQuestionDto) {
        Question question = Question.builder()
                .body(createQuestionDto.getBody())
                .correctAnswers(createQuestionDto.getCorrectAnswers())
                .published(false)
                .build();

        Question created = questionRepository.save(question);
        System.out.println(created);
        return QuestionMapper.toView(created);
    }
}
