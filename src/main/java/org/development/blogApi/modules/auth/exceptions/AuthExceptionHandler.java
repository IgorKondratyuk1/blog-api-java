package org.development.blogApi.modules.auth.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.development.blogApi.common.exceptions.dto.APIErrorResult;
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

    // For expired jwt token
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<APIErrorResult> expiredJwtException(ExpiredJwtException exception) {
        log.error("GlobalExceptionHandler JWT");
        exception.printStackTrace();

        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), "Jwt token is expired");
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    // For wrong jwt and when jwt signature does not match
    @ExceptionHandler({SignatureException.class, MalformedJwtException.class})
    public ResponseEntity<APIErrorResult> signatureException(RuntimeException exception) {
        log.error("AuthExceptionHandler SignatureException", exception);

        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<APIErrorResult> jwtTokenException(JwtTokenException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<APIErrorResult> authException(AuthException exception) {
        APIErrorResult apiErrorResult = new APIErrorResult(HttpStatus.UNAUTHORIZED.value(), exception.getMessage());
        return ResponseEntity
                .status(apiErrorResult.statusCode)
                .body(apiErrorResult);
    }
}
