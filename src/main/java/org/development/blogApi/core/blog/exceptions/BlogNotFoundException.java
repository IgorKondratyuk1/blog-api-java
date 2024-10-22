package org.development.blogApi.core.blog.exceptions;

public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException() {
        super("Blog not found");
    }
}
