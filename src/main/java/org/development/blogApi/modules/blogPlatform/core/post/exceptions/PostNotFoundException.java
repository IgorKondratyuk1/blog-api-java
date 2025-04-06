package org.development.blogApi.modules.blogPlatform.core.post.exceptions;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException() {
        super("Post not found");
    }
}
