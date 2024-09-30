package org.development.blogApi.common.utils;

public class SortHelper {
    public static String getSortBy(String value) {
        switch (value) {
            case "blogName":
                return "blog.name";
            default:
                return value;
        }
    }
}
