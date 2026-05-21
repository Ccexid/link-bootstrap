package me.link.bootstrap.domain.valueobject;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum StatusEnum {
    /**
     * 正常状态
     */
    NORMAL(0, "正常"),
    /**
     * 停用状态
     */
    DISABLE(1, "停用");

    /**
     * 状态值（存储到数据库）
     */
    @EnumValue // 标记数据库存的值
    private final Integer value;

    /**
     * 状态描述（返回给前端）
     */
    @JsonValue
    private final String desc;
}
