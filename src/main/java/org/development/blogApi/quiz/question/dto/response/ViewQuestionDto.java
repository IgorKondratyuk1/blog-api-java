package org.development.blogApi.quiz.question.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ViewQuestionDto {
    private UUID id;
    private String body;
    private List<String> correctAnswers;
    private Boolean published;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
