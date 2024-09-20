package org.development.blogApi.common.utils;

import java.util.UUID;

public class UUIDHelper {
    public static boolean isValidUUID(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
