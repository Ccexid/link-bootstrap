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
 * 支持两种分页模式：
 * <ul>
 *     <li>标准分页：{@code pageSize} 设置为 1-200 之间的值</li>
 *     <li>不分页模式：{@code pageSize} 设置为 -1，用于导出等需要查询所有数据的场景</li>
 * </ul>
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

    /**
     * 每页条数 - 不分页
     * <p>
     * 例如说，导出接口，可以设置 {@link #pageSize} 为 -1 不分页，查询所有数据。
     * </p>
     */
    public static final Integer PAGE_SIZE_NONE = -1;

    /**
     * 每页条数最大值（不包括不分页模式）
     */
    private static final Integer PAGE_SIZE_MAX = 200;

    @Schema(description = "页码，从 1 开始", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小值为 1")
    private Integer pageNo = PAGE_NO;

    /**
     * 每页条数
     * <p>有效范围：1-200，或设置为 -1 表示不分页</p>
     */
    @Schema(
            description = "每页条数，有效范围 1-200；设置为 -1 表示不分页（用于导出等场景）",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "10"
    )
    @NotNull(message = "每页条数不能为空")
    private Integer pageSize = PAGE_SIZE;

    /**
     * 获取分页大小，并进行校验
     * <p>
     * 支持两种模式：
     * <ul>
     *     <li>标准分页：pageSize 必须在 1-200 范围内</li>
     *     <li>不分页模式：pageSize 为 -1</li>
     * </ul>
     * </p>
     *
     * @return 分页大小
     * @throws IllegalArgumentException 如果 pageSize 值不合法
     */
    public Integer getPageSize() {
        if (pageSize.equals(PAGE_SIZE_NONE)) {
            return pageSize;
        }
        if (pageSize < 1 || pageSize > PAGE_SIZE_MAX) {
            throw new IllegalArgumentException(String.format("每页条数必须在 1-%d 范围内，当前值: %d", PAGE_SIZE_MAX, pageSize));
        }
        return pageSize;
    }
}
