package org.development.blogApi.modules.quiz.pairQuizGame.entity.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GamePairStatus {
    PENDING("PendingSecondPlayer"),
    ACTIVE("Active"),
    FINISHED("Finished");

    public final String value;

    GamePairStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
