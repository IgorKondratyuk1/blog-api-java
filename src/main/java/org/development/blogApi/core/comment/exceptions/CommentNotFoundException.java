package org.development.blogApi.core.comment.exceptions;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment not found");
    }
}
