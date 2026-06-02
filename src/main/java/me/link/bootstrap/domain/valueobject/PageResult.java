package me.link.bootstrap.domain.valueobject;

import java.util.List;

/**
 * 领域层分页结果值对象，用于隔离应用层和具体持久化分页实现。
 */
public record PageResult<T>(List<T> records, Long total) {
}
