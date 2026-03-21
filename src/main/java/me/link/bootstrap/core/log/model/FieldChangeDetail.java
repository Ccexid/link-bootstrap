package me.link.bootstrap.core.log.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段变更详情模型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldChangeDetail {
    /**
     * 字段名称或描述（优先取自 @Schema 注解，否则为字段名）
     */
    private String fieldName;

    /**
     * 变更前的原始值
     */
    private Object oldValue;

    /**
     * 变更后的新值
     */
    private Object newValue;
}