package org.development.blogApi.modules.auth.exceptions;

public class JwtTokenException extends RuntimeException {
    public JwtTokenException(String message) {
        super(message);
    }
}
