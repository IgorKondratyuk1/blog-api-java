package org.development.blogApi.utils;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UuidUtil {
    public static UUID extractUuidFromMessage(String message) {
        Pattern pattern = Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}");
        Matcher matcher = pattern.matcher(message);

        if (!matcher.find()) {
            return null;
        }

        return UUID.fromString(matcher.group());
    }
}
