package org.development.blogApi.modules.quiz.pairQuizGame.entity.enums;

public enum AnswerStatus {
    CORRECT("Correct"),
    INCORRECT("Incorrect");

    public final String value;

    AnswerStatus(String value) {
        this.value = value;
    }
}
