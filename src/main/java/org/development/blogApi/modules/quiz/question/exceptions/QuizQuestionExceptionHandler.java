package org.development.blogApi.modules.quiz.question.exceptions;

import org.development.blogApi.common.exceptions.dto.APIErrorResult;
import org.development.blogApi.common.exceptions.dto.APIFieldError;
import org.development.blogApi.common.exceptions.dto.APIValidationErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class QuizQuestionExceptionHandler {
    @ExceptionHandler(QuizQuestionNotFoundException.class)
    public ResponseEntity<APIErrorResult> quizQuestionNotFoundException(QuizQuestionNotFoundException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(QuizQuestionAlreadyInStateException.class)
    public ResponseEntity<APIValidationErrorResult> quizQuestionAlreadyInStateException(QuizQuestionAlreadyInStateException exception) {
        List<APIFieldError> apiErrorResult = new ArrayList<>();
        APIFieldError apiFieldError = new APIFieldError(exception.getMessage(), "published");
        apiErrorResult.add(apiFieldError);

        APIValidationErrorResult apiValidationErrorResult = new APIValidationErrorResult(apiErrorResult);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiValidationErrorResult);
    }
}
