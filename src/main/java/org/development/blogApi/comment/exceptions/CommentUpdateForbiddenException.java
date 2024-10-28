package org.development.blogApi.comment.exceptions;

import jakarta.validation.constraints.NotNull;

public class CommentUpdateForbiddenException extends RuntimeException {
    public CommentUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
