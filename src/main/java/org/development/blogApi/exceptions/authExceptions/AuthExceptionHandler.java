package org.development.blogApi.exceptions.authExceptions;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.exceptions.dto.APIErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {

    // For expired Jwt token Error
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Object> handleRuntimeException(ExpiredJwtException exception) {
        log.error("GlobalExceptionHandler JWT");
        exception.printStackTrace();

        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), "Jwt token is expired");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Object> authException(AuthException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
