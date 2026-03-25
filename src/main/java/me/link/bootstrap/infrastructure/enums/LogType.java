package me.link.bootstrap.infrastructure.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 操作日志类型枚举
 * <p>
 * 划分在基础设施层，供全域使用。
 * 定义了系统中各类操作日志的类型及其对应的中文描述和编码。
 * </p>
 *
 * @author Lingma
 */
@Getter
@AllArgsConstructor
public enum LogType {

    /**
     * 查询操作
     * 用于记录数据检索类的日志
     */
    QUERY("查询", 1),

    /**
     * 新增操作
     * 用于记录数据创建类的日志
     */
    INSERT("新增", 2),

    /**
     * 修改操作
     * 用于记录数据更新类的日志
     */
    UPDATE("修改", 3),

    /**
     * 删除操作
     * 用于记录数据移除类的日志
     */
    DELETE("删除", 4),

    /**
     * 授权操作
     * 用于记录权限分配或变更类的日志
     */
    GRANT("授权", 5),

    /**
     * 导出操作
     * 用于记录数据导出类的日志
     */
    EXPORT("导出", 6),

    /**
     * 导入操作
     * 用于记录数据导入类的日志
     */
    IMPORT("导入", 7),

    /**
     * 强退操作
     * 用于记录强制用户退出系统类的日志
     */
    FORCE("强退", 8),

    /**
     * 认证操作
     * 用于记录用户登录、登出等身份认证类的日志
     */
    AUTH("认证", 9),

    /**
     * 其它操作
     * 用于记录未归类到上述类型的其他操作日志
     */
    OTHER("其它", 0);

    /**
     * 日志类型的中文标题
     */
    private final String title;

    /**
     * 日志类型的数字编码
     */
    private final Integer code;
}