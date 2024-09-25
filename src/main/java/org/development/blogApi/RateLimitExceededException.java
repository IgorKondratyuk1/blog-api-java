package org.development.blogApi;

public class RateLimitExceededException extends RuntimeException {
    public RateLimitExceededException(String m) {
        super(m);
    }
}
