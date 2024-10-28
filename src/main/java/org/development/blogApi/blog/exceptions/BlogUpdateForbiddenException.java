package org.development.blogApi.blog.exceptions;

import jakarta.validation.constraints.NotNull;

public class BlogUpdateForbiddenException extends RuntimeException {
    public BlogUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
