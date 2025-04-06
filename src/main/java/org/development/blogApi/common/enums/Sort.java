package org.development.blogApi.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import org.development.blogApi.modules.blogPlatform.core.like.enums.LikeStatus;


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

    public static LikeStatus fromValue(String value) {
        for (LikeStatus status : LikeStatus.values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid value: " + value);
    }
}
