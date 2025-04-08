package org.development.blogApi.modules.quiz.pairQuizGame.exceptions;

public class GamePairNotFoundException extends RuntimeException {
    public GamePairNotFoundException() {
        super("GamePair not found");
    }
}
