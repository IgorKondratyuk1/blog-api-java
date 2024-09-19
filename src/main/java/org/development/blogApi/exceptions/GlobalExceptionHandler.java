package org.development.blogApi.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.atn.SemanticContext;
import org.development.blogApi.exceptions.dto.APIErrorResult;
import org.development.blogApi.exceptions.dto.APIFieldError;
import org.development.blogApi.exceptions.dto.APIValidationErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // For field validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleRuntimeException(MethodArgumentNotValidException exception) {
        log.info("GlobalExceptionHandler MethodArgumentNotValidException");

        List<APIFieldError> errors = new ArrayList<>();
        exception.getAllErrors().forEach(e -> {
            APIFieldError apiFieldError = new APIFieldError(((FieldError) e).getField(), e.getDefaultMessage());
            errors.add(apiFieldError);
        });

        APIValidationErrorResult apiErrorResult = new APIValidationErrorResult(errors);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResult);
    }

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

    // For not valid routes
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> noHandlerFoundException() {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), "Path not Found");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        log.error("GlobalExceptionHandler Exception");
        exception.printStackTrace();

        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.BAD_REQUEST.value(), "Oops, something went wrong");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
