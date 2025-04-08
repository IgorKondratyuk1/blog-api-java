package org.development.blogApi.modules.quiz.pairQuizGame.exceptions;

public class WrongStateOfGamePairException extends RuntimeException {
    public WrongStateOfGamePairException(String message) {
        super("Wrong state of game pair: " + message);
    }
}
