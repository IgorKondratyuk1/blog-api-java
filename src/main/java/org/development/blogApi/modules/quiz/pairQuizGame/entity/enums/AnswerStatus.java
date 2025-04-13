package org.development.blogApi.modules.quiz.pairQuizGame.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AnswerStatus {
    CORRECT("Correct"),
    INCORRECT("Incorrect");

    public final String value;

    AnswerStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
