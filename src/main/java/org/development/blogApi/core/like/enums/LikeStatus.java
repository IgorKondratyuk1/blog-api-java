package org.development.blogApi.core.like.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LikeStatus {
    NONE("None"),
    LIKE("Like"),
    DISLIKE("Dislike");

    private final String value;

    LikeStatus(String value) {
        this.value = value;
    }


    @JsonValue
    public String getValue() {
        return value;
    }
}
