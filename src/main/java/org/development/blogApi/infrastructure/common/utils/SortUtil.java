package org.development.blogApi.infrastructure.common.utils;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Path;

public class SortUtil {
    public static String getSortBy(String value) {
        switch (value) {
            case "blogName":
                return "blog.name";
            default:
                return value;
        }
    }

    public static Path<?> getNestedPath(Root<?> root, String nestedPath) {
        String[] attributes = nestedPath.split("\\.");
        Path<?> path = root;

        for (String attribute : attributes) {
            path = path.get(attribute);
        }

        return path;
    }
}
