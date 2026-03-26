package me.link.bootstrap.infrastructure.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serial;

/**
 * 租户基础实体基类
 * 优化点：
 * 1. 增加 ToString 显式调用父类，方便日志排查
 * 2. 字段访问权限改为 protected
 * 3. 租户 ID 增加 index 索引提示（逻辑层面）
 * 4. 显式声明序列化 ID
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public abstract class BaseTenantEntity extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 租户 ID 用String解决前端Number丢失问题
     * 优化：设置为 protected 方便子类逻辑处理
     * 注意：在开启 MP 自动解析多租户时，此字段通常由 TenantLineHandler 维护
     */
    @TableField(fill = FieldFill.INSERT)
    protected String tenantId;
}