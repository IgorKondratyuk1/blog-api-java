package org.development.blogApi.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CookieUtil {
    static public Optional<String> getValueByKey(List<String> cookies, String key) {
        return cookies.stream()
                .flatMap(cookie -> Arrays.stream(cookie.split(";")))
                .map(String::trim) // Trim spaces around cookie parts
                .filter(c -> c.startsWith(key + "="))
                .map(c -> c.substring(key.length() + 1))
                .findFirst();
    }
}
