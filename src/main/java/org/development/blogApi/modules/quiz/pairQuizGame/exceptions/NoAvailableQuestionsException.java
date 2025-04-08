package org.development.blogApi.modules.quiz.pairQuizGame.exceptions;

public class NoAvailableQuestionsException extends RuntimeException {
    public NoAvailableQuestionsException() {
        super("All questions have been answered. No available questions");
    }
}
