package org.development.blogApi.modules.quiz.question.exceptions;

public class QuizQuestionNotFoundException extends RuntimeException {
    public QuizQuestionNotFoundException() {
        super("QuizQuestion not found");
    }
}
