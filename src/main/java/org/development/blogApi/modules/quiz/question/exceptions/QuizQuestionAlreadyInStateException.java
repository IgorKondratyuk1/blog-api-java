package org.development.blogApi.modules.quiz.question.exceptions;

public class QuizQuestionAlreadyInStateException extends RuntimeException {
    public QuizQuestionAlreadyInStateException() {
        super("QuizQuestion already published");
    }
}
