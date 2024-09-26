package org.development.blogApi.exceptions.postExceptions;

import org.development.blogApi.exceptions.dto.APIErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PostExceptionHandler {
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Object> postNotFoundException(PostNotFoundException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(PostUpdateForbiddenException.class)
    public ResponseEntity<Object> postUpdateForbiddenException(PostUpdateForbiddenException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(IncorrectPostDataException.class)
    public ResponseEntity<Object> incorrectPostDataException(IncorrectPostDataException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
