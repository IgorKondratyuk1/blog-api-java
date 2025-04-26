package org.development.blogApi.common.exceptions;

import com.fasterxml.jackson.databind.JsonMappingException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.common.exceptions.dto.APIErrorResult;
import org.development.blogApi.common.exceptions.dto.APIFieldError;
import org.development.blogApi.common.exceptions.dto.APIValidationErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    // For field validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIValidationErrorResult> handleRuntimeException(MethodArgumentNotValidException exception) {
        log.info("GlobalExceptionHandler MethodArgumentNotValidException");

        List<APIFieldError> errors = new ArrayList<>();
        exception.getAllErrors().forEach(e -> {
            APIFieldError apiFieldError = new APIFieldError(e.getDefaultMessage(), ((FieldError) e).getField());
            errors.add(apiFieldError);
        });

        // Set to track fields that we have already seen
        Set<String> seenFields = new HashSet<>();

        // Filter the list to include only unique fields
        List<APIFieldError> uniqueErrorFields = errors.stream()
                .filter(error -> seenFields.add(error.getField())) // Add to set and check if added successfully
                .collect(Collectors.toList());

        APIValidationErrorResult apiErrorResult = new APIValidationErrorResult(uniqueErrorFields);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResult);
    }

    @ExceptionHandler(JsonMappingException.class)
    public ResponseEntity<APIValidationErrorResult> jsonMappingException(JsonMappingException exception) {
        log.info("GlobalExceptionHandler JsonMappingException");

        List<APIFieldError> errors = new ArrayList<>();
        exception.getPath().forEach(e -> {
            APIFieldError apiFieldError = new APIFieldError(exception.getMessage(), e.getFieldName());
            errors.add(apiFieldError);
        });

        // Set to track fields that we have already seen
        Set<String> seenFields = new HashSet<>();

        // Filter the list to include only unique fields
        List<APIFieldError> uniqueErrorFields = errors.stream()
                .filter(error -> seenFields.add(error.getField())) // Add to set and check if added successfully
                .collect(Collectors.toList());

        APIValidationErrorResult apiErrorResult = new APIValidationErrorResult(uniqueErrorFields);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(apiErrorResult);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.info("GlobalExceptionHandler HttpMessageNotReadableException");
        Throwable cause = ex.getCause();

        if (cause instanceof JsonMappingException) {
            return this.jsonMappingException((JsonMappingException) cause);
        } else {
            return this.handleRuntimeException((RuntimeException) cause);
        }
    }

    // For not valid routes
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIErrorResult> noHandlerFoundException() {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), "Path not Found");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    // Request params type mismatch
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<APIErrorResult> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.BAD_REQUEST.value(), "Wrong parameter or request body type, with name \"" + exception.getName() + "\"");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<APIErrorResult> handleRuntimeException(RuntimeException exception) {
        log.error("GlobalExceptionHandler caught error:", exception);
        exception.printStackTrace();

        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.BAD_REQUEST.value(), "Oops, something went wrong");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
