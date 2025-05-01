package org.development.blogApi.modules.quiz.pairQuizGame.utils;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.List;

public class SortStatisticUtil {

    public static <T> void sortListByFields(List<T> list, List<String> sortByFields) {
        Comparator<T> comparator = null;

        for (String sort : sortByFields) {
            String[] parts = sort.trim().split("\\s+");
            String fieldName = parts[0];
            boolean descending = parts.length > 1 && parts[1].equalsIgnoreCase("desc");

            Comparator<T> fieldComparator = Comparator.comparing(o -> {
                try {
                    Field field = o.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return (Comparable) field.get(o);
                } catch (Exception e) {
                    throw new RuntimeException("Error accessing field: " + fieldName, e);
                }
            });

            if (descending) {
                fieldComparator = fieldComparator.reversed();
            }

            comparator = (comparator == null) ? fieldComparator : comparator.thenComparing(fieldComparator);
        }

        if (comparator != null) {
            list.sort(comparator);
        }
    }
}
