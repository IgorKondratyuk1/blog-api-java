package org.development.blogApi.modules.quiz.pairQuizGame.exceptions;

import org.development.blogApi.common.exceptions.dto.APIErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GamePairExceptionHandler {
    @ExceptionHandler(GamePairNotFoundException.class)
    public ResponseEntity<APIErrorResult> gamePairNotFoundException(GamePairNotFoundException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(UserAlreadyHasGamePairException.class)
    public ResponseEntity<APIErrorResult> userAlreadyHasGamePairException(UserAlreadyHasGamePairException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(NoAvailableQuestionsException.class)
    public ResponseEntity<APIErrorResult> noAvailableQuestionsException(NoAvailableQuestionsException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(WrongStateOfGamePairException.class)
    public ResponseEntity<APIErrorResult> wrongStateOfGamePairException(WrongStateOfGamePairException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(GamePairForbiddenException.class)
    public ResponseEntity<APIErrorResult> gamePairForbiddenException(GamePairForbiddenException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
