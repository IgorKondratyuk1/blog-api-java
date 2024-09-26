package org.development.blogApi.exceptions.postExceptions;

import jakarta.validation.constraints.NotNull;

public class PostUpdateForbiddenException extends RuntimeException {
    public PostUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
