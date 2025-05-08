package org.development.blogApi.modules.blogPlatform.core.blog.exceptions;

import org.development.blogApi.common.exceptions.dto.APIErrorResult;
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
    public ResponseEntity<APIErrorResult> blogNotFoundException(BlogNotFoundException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(BlogChangeForbiddenException.class)
    public ResponseEntity<APIErrorResult> blogUpdateForbiddenException(BlogChangeForbiddenException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
