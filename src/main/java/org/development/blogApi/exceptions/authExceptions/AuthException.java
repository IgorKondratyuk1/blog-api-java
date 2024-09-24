package org.development.blogApi.exceptions.authExceptions;

public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
