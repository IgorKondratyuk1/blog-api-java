package org.development.blogApi.modules.quiz.pairQuizGame.exceptions;

public class UserAlreadyHasGamePairException extends RuntimeException {
    public UserAlreadyHasGamePairException(String gamePairId) {
        super("User has already had active game pair: " + gamePairId);
    }
}
