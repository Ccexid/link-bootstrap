package me.link.bootstrap.infrastructure.entity; // 建议目录见下文

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础实体基类
 * 优化点：
 * 1. 增加 Accessors 支持链式调用
 * 2. 字段访问权限改为 protected，方便子类覆盖
 * 3. 逻辑删除和版本号增加 @JsonIgnore，避免接口暴露敏感/无用信息
 * 4. 统一日期格式化常量，减少硬编码
 */
@Data
@Accessors(chain = true) // 允许 user.setName("X").setAge(18)
public abstract class BaseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID (分布式唯一ID) 用String 解决前端框架无法识别Long类型
     */
    @TableId(type = IdType.ASSIGN_ID)
    protected String id;

    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String updateBy;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected LocalDateTime updateTime;

    /**
     * 是否删除 (0 未删除, 1 已删除)
     * 优化：通常前端不需要知道逻辑删除状态，建议隐藏
     */
    @JsonIgnore
    @TableLogic
    @TableField(fill = FieldFill.INSERT)
    protected Integer deleted;

    /**
     * 乐观锁版本号
     * 优化：前端仅在做并发控制时需要，默认建议隐藏
     */
    @JsonIgnore
    @Version
    @TableField(fill = FieldFill.INSERT)
    protected Integer version;
}