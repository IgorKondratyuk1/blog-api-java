package org.development.blogApi.core.like.enums;

public enum LikeLocation {
    COMMENT("Comment"),
    POST("Post");

    private final String value;

    LikeLocation(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
