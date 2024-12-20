package org.development.blogApi.post.exceptions;

import jakarta.validation.constraints.NotNull;

public class IncorrectPostDataException extends RuntimeException {
    public IncorrectPostDataException(@NotNull String message) {
        super(message);
    }
}
