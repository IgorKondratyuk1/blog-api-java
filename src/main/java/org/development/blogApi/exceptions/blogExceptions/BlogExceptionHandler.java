package org.development.blogApi.exceptions.blogExceptions;

import org.development.blogApi.exceptions.dto.APIErrorResult;
import org.development.blogApi.exceptions.postExceptions.PostUpdateForbiddenException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BlogExceptionHandler {
    @ExceptionHandler(BlogNotFoundException.class)
    public ResponseEntity<Object> blogNotFoundException(BlogNotFoundException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(BlogUpdateForbiddenException.class)
    public ResponseEntity<Object> blogUpdateForbiddenException(BlogUpdateForbiddenException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
