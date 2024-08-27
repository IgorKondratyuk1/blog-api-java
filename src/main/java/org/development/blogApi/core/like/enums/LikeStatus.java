package org.development.blogApi.core.like.enums;

public enum LikeStatus {
    NONE("None"),
    LIKE("Like"),
    DISLIKE("Dislike");

    private final String value;

    LikeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
