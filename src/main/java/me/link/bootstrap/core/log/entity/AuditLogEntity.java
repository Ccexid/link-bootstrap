package me.link.bootstrap.core.log.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Builder;
import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import me.link.bootstrap.core.log.model.FieldChangeDetail;

import java.util.Date;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "audit_log", autoResultMap = true)
public class AuditLogEntity {

    @TableId(type = IdType.ASSIGN_ID) // 雪花算法 ID
    private Long id;

    private String tenantId;    // 租户ID
    private String module;      // 模块
    private String operation;   // 操作
    private String businessId;  // 业务ID
    private String operator;    // 操作人
    private String costTime;    // 耗时
    private String status;      // 状态
    private String errorMsg;    // 错误信息

    /**
     * 关键：存储字段变更详情（JSON格式）
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<FieldChangeDetail> changes;

    @TableField(fill = FieldFill.INSERT) // 自动填充插入时间
    private Date createTime;
}