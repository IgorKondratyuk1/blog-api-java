package org.development.blogApi.modules.quiz.pairQuizGame.entity.enums;

public enum GamePairStatus {
    PENDING("PendingSecondPlayer"),
    ACTIVE("Active"),
    FINISHED("Finished");

    public final String value;

    GamePairStatus(String value) {
        this.value = value;
    }
}
