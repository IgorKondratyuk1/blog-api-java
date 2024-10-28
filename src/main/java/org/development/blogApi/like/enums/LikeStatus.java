package org.development.blogApi.like.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum LikeStatus implements ValueEnum<String> {
    NONE("None"),
    LIKE("Like"),
    DISLIKE("Dislike");

    private final String value;

    LikeStatus(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    public static LikeStatus fromValue(String value) {
        for (LikeStatus status : LikeStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
