package org.development.blogApi.exceptions.blogExceptions;

import jakarta.validation.constraints.NotNull;

public class BlogUpdateForbiddenException extends RuntimeException {
    public BlogUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
