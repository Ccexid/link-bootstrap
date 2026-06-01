package me.link.bootstrap.shared.kernel.util;

import cn.hutool.core.util.StrUtil;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class SortableFieldUtils {

    private SortableFieldUtils() {
    }

    public static List<String> parseSortableFields(Class<?> clazz) {
        Set<String> fieldsSet = new LinkedHashSet<>();
        Class<?> currentClass = clazz;

        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                if (field.isAnnotationPresent(Sortable.class)) {
                    Sortable annotation = field.getAnnotation(Sortable.class);
                    String fieldName = StrUtil.isNotBlank(annotation.value())
                            ? annotation.value()
                            : StrUtil.toUnderlineCase(field.getName());
                    fieldsSet.add(fieldName);
                }
            }
            currentClass = currentClass.getSuperclass();
        }

        if (fieldsSet.isEmpty()) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableList(new ArrayList<>(fieldsSet));
    }
}
