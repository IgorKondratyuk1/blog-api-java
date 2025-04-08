package org.development.blogApi.modules.blogPlatform.core.comment.exceptions;

import jakarta.validation.constraints.NotNull;

public class CommentUpdateForbiddenException extends RuntimeException {
    public CommentUpdateForbiddenException(@NotNull String message) {
        super(message);
    }
}
