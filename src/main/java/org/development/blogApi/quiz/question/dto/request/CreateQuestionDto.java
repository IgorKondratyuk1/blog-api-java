package org.development.blogApi.quiz.question.dto.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateQuestionDto {

    @NotEmpty(message = "Body can not be empty")
    private String body;

    @NotEmpty(message = "CorrectAnswers can not be empty")
    private List<String> correctAnswers;
}
