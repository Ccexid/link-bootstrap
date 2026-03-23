package me.link.bootstrap.core.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import me.link.bootstrap.core.log.model.FieldChangeDetail;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志实体类
 * 适配 P2S2B2C 架构，记录关键业务操作及数据变更 (Diff)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "audit_log", autoResultMap = true)
@Schema(description = "审计日志实体")
public class AuditLogEntity {

    /**
     * 主键 ID
     * 对应 SQL: `id` bigint
     */
    @TableId(type = IdType.ASSIGN_ID)
    @Schema(description = "主键 ID")
    private Long id;

    /**
     * 租户 ID
     * 对应 SQL: `tenant_id` bigint
     * 注意：必须使用 Long 类型以适配 TenantContextHolder 转换逻辑
     */
    @Schema(description = "租户 ID")
    private Long tenantId;

    /**
     * 操作模块
     * 对应 SQL: `module` varchar(64)
     */
    @Schema(description = "操作模块")
    private String module;

    /**
     * 操作描述
     * 对应 SQL: `operation` varchar(128)
     */
    @Schema(description = "操作描述")
    private String operation;

    /**
     * 业务主键/关联 ID
     * 对应 SQL: `business_id` varchar(64)
     */
    @Schema(description = "业务主键/关联 ID")
    private String businessId;

    /**
     * 操作人账号
     * 对应 SQL: `operator` varchar(64)
     */
    @Schema(description = "操作人账号")
    private String operator;

    /**
     * 耗时 (毫秒)
     * 对应 SQL: `cost_time` int unsigned
     */
    @Schema(description = "耗时 (毫秒)")
    private Long costTime;

    /**
     * 操作状态 (SUCCESS, FAIL)
     * 对应 SQL: `status` varchar(20)
     */
    @Schema(description = "操作状态 (SUCCESS, FAIL)")
    private String status;

    /**
     * 异常堆栈信息
     * 对应 SQL: `error_msg` text
     */
    @Schema(description = "异常堆栈信息")
    private String errorMsg;

    /**
     * 变更明细 (JSON 格式)
     * 对应 SQL: `changes` json
     * 使用 JacksonTypeHandler 自动实现 List <-> JSON 转换
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    @Schema(description = "变更明细 (JSON 格式)")
    private List<FieldChangeDetail> changes;

    /**
     * 创建时间
     * 对应 SQL: `create_time` datetime
     * 由 MybatisPlusHandler 自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}