package org.development.blogApi.exceptions.authExceptions;

import org.development.blogApi.exceptions.dto.APIErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> authException(AuthException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
