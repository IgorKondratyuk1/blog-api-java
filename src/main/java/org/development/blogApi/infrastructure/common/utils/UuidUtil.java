package org.development.blogApi.infrastructure.common.utils;

import java.util.UUID;

public class UuidUtil {
    public static boolean isValidUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
