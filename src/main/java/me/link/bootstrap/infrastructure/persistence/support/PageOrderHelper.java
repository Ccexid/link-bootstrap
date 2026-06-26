package me.link.bootstrap.infrastructure.persistence.support;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * MyBatis-Plus 分页排序辅助类。
 */
public final class PageOrderHelper {

    private PageOrderHelper() {
    }

    public static void applyOrders(Page<?> page, List<SortingField> sortingFields, Map<String, String> sortFieldMapping) {
        if (page == null || sortingFields == null || sortingFields.isEmpty()) {
            return;
        }
        sortingFields.stream()
                .map(sortingField -> toOrderItem(sortingField, sortFieldMapping))
                .filter(Objects::nonNull)
                .forEach(page::addOrder);
    }

    private static OrderItem toOrderItem(SortingField sortingField, Map<String, String> sortFieldMapping) {
        if (sortingField == null || sortFieldMapping == null || sortFieldMapping.isEmpty()) {
            return null;
        }
        String column = sortFieldMapping.get(sortingField.getField());
        if (column == null) {
            return null;
        }
        if (sortingField.isAsc()) {
            return OrderItem.asc(column);
        }
        return OrderItem.desc(column);
    }
}
