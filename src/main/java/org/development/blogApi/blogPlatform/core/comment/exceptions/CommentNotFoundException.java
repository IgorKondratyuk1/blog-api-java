package org.development.blogApi.blogPlatform.core.comment.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found");
    }
}
