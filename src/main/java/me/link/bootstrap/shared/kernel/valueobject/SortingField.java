package me.link.bootstrap.shared.kernel.valueobject;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 排序字段实体类
 * <p>
 * 用于封装排序相关的字段信息，包括字段标识和排序方向。
 * </p>
 * <p>
 * <strong>安全提示：</strong>{@code field} 字段值将被用于 SQL ORDER BY 子句，
 * 建议使用合法的数据库字段名格式（字母、数字、下划线、点号组成），
 * 例如: "createTime", "user.name", "order_total"
 * </p>
 *
 * @author Ccexid
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "排序对象")
public class SortingField {

    /**
     * 字段标识 (Field) 
     * <p>
     * 用于指定需要排序的字段名称或唯一标识符。
     * 支持嵌套字段名（如 "user.name"）和单字段名（如 "createTime"）。
     * </p>
     */
    @Schema(description = "字段标识，支持嵌套字段名", example = "createTime")
    @Pattern(regexp = "^[a-zA-Z0-9_.]+$", message = "字段名只能包含字母、数字、下划线和点号")
    private String field;

    /**
     * 是否升序
     * <p>
     * true 表示升序排列 (ASC)，false 表示降序排列 (DESC)。
     * 默认为 false（降序）。
     * </p>
     */
    @Schema(description = "是否升序，真值表示升序，假值表示降序，默认为假", example = "true")
    private boolean asc;
}
