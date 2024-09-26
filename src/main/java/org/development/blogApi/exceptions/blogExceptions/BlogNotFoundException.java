package org.development.blogApi.exceptions.blogExceptions;

public class BlogNotFoundException extends RuntimeException {
    public BlogNotFoundException() {
        super("Blog not found");
    }
}
