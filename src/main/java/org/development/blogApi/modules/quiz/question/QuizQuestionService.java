package org.development.blogApi.modules.quiz.question;

import lombok.AllArgsConstructor;
import org.development.blogApi.modules.quiz.question.dto.request.CreateQuestionDto;
import org.development.blogApi.modules.quiz.question.dto.request.UpdateQuestionDto;
import org.development.blogApi.modules.quiz.question.dto.request.UpdateQuestionPublishStatusDto;
import org.development.blogApi.modules.quiz.question.dto.response.ViewQuestionDto;
import org.development.blogApi.modules.quiz.question.entity.QuizQuestionEntity;
import org.development.blogApi.modules.quiz.question.exceptions.QuizQuestionNotFoundException;
import org.development.blogApi.modules.quiz.question.repository.QuizQuestionRepository;
import org.development.blogApi.modules.quiz.question.utils.QuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class QuizQuestionService {

    private final QuizQuestionRepository quizQuestionRepository;

    public QuizQuestionEntity findQuestionById(String questionId) {
        return this.quizQuestionRepository.findById(UUID.fromString(questionId))
                .orElseThrow(() -> new QuizQuestionNotFoundException());
    }

    public boolean isAnswerToQuestionCorrect(String questionId, String answer) {
        QuizQuestionEntity quizQuestionEntity = this.findQuestionById(questionId);
        List<String> questionCorrectAnswers = quizQuestionEntity.getCorrectAnswers();
        String trimmedAnswer = answer.trim().toLowerCase();

        return questionCorrectAnswers.stream()
                .anyMatch(question -> question.equalsIgnoreCase(trimmedAnswer));
    }

    public List<QuizQuestionEntity> getRandomQuestionByQuantity(int quantity) {
        return this.quizQuestionRepository.findRandomQuestionsByQuantity(quantity);
    }

    public ViewQuestionDto createQuestion(CreateQuestionDto createQuestionDto) {
        QuizQuestionEntity quizQuestionEntity = QuizQuestionEntity.builder()
                .body(createQuestionDto.body())
                .correctAnswers(createQuestionDto.correctAnswers())
                .published(false)
                .build();

        QuizQuestionEntity created = quizQuestionRepository.save(quizQuestionEntity);
        System.out.println(created);
        return QuestionMapper.toView(created);
    }

    public void updateQuestion(String id, UpdateQuestionDto updateQuestionDto) {
        QuizQuestionEntity quizQuestionEntity = this.quizQuestionRepository.findById(UUID.fromString(id)).orElseThrow(() -> new QuizQuestionNotFoundException());

        quizQuestionEntity.setBody(updateQuestionDto.body());
        quizQuestionEntity.setCorrectAnswers(updateQuestionDto.correctAnswers());
        this.quizQuestionRepository.save(quizQuestionEntity);
    }

    public void updateQuestionPublishStatus(String id, UpdateQuestionPublishStatusDto updateQuestionPublishStatusDto) {
        QuizQuestionEntity quizQuestionEntity = this.quizQuestionRepository.findById(UUID.fromString(id)).orElseThrow(() -> new QuizQuestionNotFoundException());

        quizQuestionEntity.setPublished(updateQuestionPublishStatusDto.published());
        this.quizQuestionRepository.save(quizQuestionEntity);
    }

    public void deleteQuestionById(String id) {
        QuizQuestionEntity quizQuestion = this.findQuestionById(id);
        this.quizQuestionRepository.deleteById(quizQuestion.getId());
    }
}
