package me.link.bootstrap.application.command;

import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;

/**
 * 操作日志分页查询对象，封装获取分页操作日志列表所需的查询条件。
 * <p>
 * 租户ID将从当前登录用户的上下文中自动获取，无需前端传入。
 * </p>
 */
public record OperateLogPageQuery(Integer pageNo, Integer pageSize, String traceId, Long userId, String module, Integer operation, Long bizId, Boolean success, List<SortingField> sortingFields) {
}
