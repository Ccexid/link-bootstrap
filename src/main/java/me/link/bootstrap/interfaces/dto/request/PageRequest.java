package me.link.bootstrap.interfaces.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 分页请求参数
 * <p>
 * 标准分页：{@code pageSize} 设置为 1-200 之间的值
 * </p>
 *
 * @author Ccexid
 */
@Schema(description = "分页参数")
@Data
public class PageRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private static final Integer PAGE_NO = 1;
    private static final Integer PAGE_SIZE = 10;

    private static final Integer PAGE_SIZE_MAX = 200;

    @Schema(description = "页码，从 1 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = PAGE_NO;

    /**
     * 每页条数
     * <p>有效范围：1-200</p>
     */
    @Schema(
            description = "每页条数，有效范围 1-200",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "10"
    )
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize = PAGE_SIZE;

    /**
     * 获取分页大小，并进行校验
     * <p>
     * pageSize 必须在 1-200 范围内
     * </p>
     *
     * @return 分页大小
     * @throws IllegalArgumentException 如果 pageSize 值不合法
     */
    public Integer getPageSize() {
        if (pageSize == null) {
            throw new IllegalArgumentException("每页条数不能为空");
        }
        if (pageSize < 1 || pageSize > PAGE_SIZE_MAX) {
            throw new IllegalArgumentException(String.format("每页条数必须在 1-%d 范围内，当前值: %d", PAGE_SIZE_MAX, pageSize));
        }
        return pageSize;
    }
}
