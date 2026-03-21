package me.link.bootstrap.core.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.link.bootstrap.core.log.model.FieldChangeDetail;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志实体类
 * 用于记录系统中的关键操作日志，包括操作人、操作模块、业务数据变更详情等
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "audit_log", autoResultMap = true)
public class AuditLogEntity {

    /**
     * 主键 ID
     * 使用雪花算法生成唯一标识
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户 ID
     * 用于多租户环境下的数据隔离
     */
    private String tenantId;

    /**
     * 操作模块
     * 标识当前操作所属的业务模块名称
     */
    private String module;

    /**
     * 操作类型
     * 描述具体执行的操作行为，如新增、修改、删除等
     */
    private String operation;

    /**
     * 业务 ID
     * 关联的业务数据唯一标识
     */
    private String businessId;

    /**
     * 操作人
     * 执行当前操作的用户标识或用户名
     */
    private String operator;

    /**
     * 耗时
     * 操作执行所消耗的时间（单位：毫秒）
     */
    private String costTime;

    /**
     * 操作状态
     * 标识操作执行的结果状态，如成功、失败等
     */
    private String status;

    /**
     * 错误信息
     * 当操作失败时记录的详细异常信息
     */
    private String errorMsg;

    /**
     * 字段变更详情列表
     * 存储操作前后字段值的变化情况，序列化为 JSON 格式存入数据库
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<FieldChangeDetail> changes;

    /**
     * 创建时间
     * 记录日志生成的时间，由框架自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}