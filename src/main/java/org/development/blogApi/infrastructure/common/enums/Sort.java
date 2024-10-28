package org.development.blogApi.infrastructure.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;


public enum Sort implements ValueEnum<String> {
    ASC("asc"),
    DESC("desc");

    private final String value;

    Sort(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String getValue() {
        return value;
    }

    public static org.development.blogApi.like.enums.LikeStatus fromValue(String value) {
        for (org.development.blogApi.like.enums.LikeStatus status : org.development.blogApi.like.enums.LikeStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
