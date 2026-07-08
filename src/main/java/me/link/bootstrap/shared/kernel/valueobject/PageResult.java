package me.link.bootstrap.shared.kernel.valueobject;

import java.util.Collections;
import java.util.List;

/**
 * 通用分页结果值对象，用于隔离应用层和具体持久化分页实现。
 * <p>
 * 该对象只表达分页查询结果，不承载 MyBatis-Plus 等基础设施类型，避免上层接口
 * 依赖具体 ORM。构造时会将空列表归一化为不可变空集合，并将空总数归一化为 0，
 * 便于接口层安全地做 stream/map 转换。
 * </p>
 */
public record PageResult<T>(List<T> records, Long total) {

    /**
     * 创建分页结果实例。
     */
    public PageResult {
        records = records == null ? Collections.emptyList() : List.copyOf(records);
        total = total == null ? 0L : total;
    }
}
