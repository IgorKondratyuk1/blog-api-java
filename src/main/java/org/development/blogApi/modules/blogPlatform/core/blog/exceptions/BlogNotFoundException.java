package org.development.blogApi.modules.blogPlatform.core.blog.exceptions;

public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException() {
        super("Blog not found");
    }
}
