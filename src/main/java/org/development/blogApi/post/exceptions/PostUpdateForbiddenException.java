package org.development.blogApi.post.exceptions;

import jakarta.validation.constraints.NotNull;

public class PostUpdateForbiddenException extends RuntimeException {
    public PostUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
