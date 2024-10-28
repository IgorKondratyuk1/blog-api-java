package org.development.blogApi.like.enums;

public enum LikeLocation implements ValueEnum<String> {
    COMMENT("Comment"),
    POST("Post");

    private final String value;

    LikeLocation(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }
}
