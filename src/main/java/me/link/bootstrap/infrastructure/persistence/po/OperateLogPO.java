package me.link.bootstrap.infrastructure.persistence.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantBaseDO;

import java.time.LocalDateTime;

/**
 * 操作日志持久化对象，字段与 system_operate_log 表结构保持对应。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "system_operate_log", autoResultMap = true)
public class OperateLogPO extends TenantBaseDO {

    @TableId
    private Long id;
    @TableField(value = "trace_id")
    private String traceId;
    @TableField(value = "user_id")
    private Long userId;
    @TableField(value = "user_type")
    private Integer userType;
    @TableField(value = "user_ip")
    private String userIp;
    @TableField(value = "user_agent")
    private String userAgent;
    @TableField(value = "module")
    private String module;
    @TableField(value = "operation")
    private Integer operation;
    @TableField(value = "biz_id")
    private Long bizId;
    @TableField(value = "action")
    private String action;
    @TableField(typeHandler = JacksonTypeHandler.class, value = "extra")
    private String extra;
    @TableField(value = "success")
    private Boolean success;
    @TableField(value = "request_method")
    private String requestMethod;
    @TableField(value = "request_url")
    private String requestUrl;
    @TableField(value = "duration")
    private Integer duration;
}
