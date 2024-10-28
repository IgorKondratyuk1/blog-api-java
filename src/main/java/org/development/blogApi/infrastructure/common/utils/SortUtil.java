package org.development.blogApi.infrastructure.common.utils;

public class SortUtil {
    public static String getSortBy(String value) {
        switch (value) {
            case "blogName":
                return "blog.name";
            default:
                return value;
        }
    }
}
