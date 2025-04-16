package org.development.blogApi.modules.quiz.question.dto.request;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotNull;
import org.development.blogApi.modules.quiz.question.util.StrictBooleanDeserializer;

public record UpdateQuestionPublishStatusDto(
        @JsonDeserialize(using = StrictBooleanDeserializer.class)
        @NotNull(message = "Published can not be empty")
        Boolean published
) {}
