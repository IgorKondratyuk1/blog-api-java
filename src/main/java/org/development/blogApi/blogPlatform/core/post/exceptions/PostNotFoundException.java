package org.development.blogApi.blogPlatform.core.post.exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super("Post not found");
    }
}
