package org.development.blogApi.common.utils;

public class PaginationUtil {

    public static int getSkipValue(int pageNumber, int pageSize) {
        return (pageNumber - 1) * pageSize;
    }

    public static int getSortValue(String sortDirection) {
        return "asc".equalsIgnoreCase(sortDirection) ? 1 : -1;
    }

    public static int getPagesCount(long totalCount, long pageSize) {
        return (int) Math.ceil((double) totalCount / pageSize);
    }
}
