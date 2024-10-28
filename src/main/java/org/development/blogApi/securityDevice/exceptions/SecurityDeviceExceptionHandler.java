package org.development.blogApi.securityDevice.exceptions;

import org.development.blogApi.infrastructure.common.exceptions.dto.APIErrorResult;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityDeviceExceptionHandler {
    @ExceptionHandler(SecurityDeviceForbiddenException.class)
    public ResponseEntity<APIErrorResult> authException(SecurityDeviceForbiddenException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.FORBIDDEN.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(SecurityDeviceNotFoundException.class)
    public ResponseEntity<APIErrorResult> authException(SecurityDeviceNotFoundException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.NOT_FOUND.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
