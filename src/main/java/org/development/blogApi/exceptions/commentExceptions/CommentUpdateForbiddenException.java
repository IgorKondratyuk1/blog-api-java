package org.development.blogApi.exceptions.commentExceptions;

import jakarta.validation.constraints.NotNull;

public class CommentUpdateForbiddenException extends RuntimeException {
    public CommentUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
