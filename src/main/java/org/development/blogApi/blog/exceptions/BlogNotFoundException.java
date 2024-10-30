package org.development.blogApi.blog.exceptions;

public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException() {
        super("Blog not found");
    }
}
