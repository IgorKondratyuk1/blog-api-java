package org.development.blogApi.modules.blogPlatform.core.blog.exceptions;

import jakarta.validation.constraints.NotNull;

public class BlogChangeForbiddenException extends RuntimeException {
    public BlogChangeForbiddenException(@NotNull String message) {
        super(message);
    }
}
