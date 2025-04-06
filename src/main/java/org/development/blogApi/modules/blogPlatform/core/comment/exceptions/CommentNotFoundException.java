package org.development.blogApi.modules.blogPlatform.core.comment.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found");
    }
}
