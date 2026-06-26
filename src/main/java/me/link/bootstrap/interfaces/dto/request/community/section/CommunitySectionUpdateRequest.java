package me.link.bootstrap.interfaces.dto.request.community.section;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新社区板块请求。
 */
@Data
@Schema(description = "更新社区板块请求")
public class CommunitySectionUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotBlank(message = "板块名称不能为空")
    @Size(max = 60, message = "板块名称不能超过60个字符")
    @Schema(description = "板块名称")
    private String name;

    @NotBlank(message = "板块编码不能为空")
    @Size(max = 64, message = "板块编码不能超过64个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{2,64}$", message = "板块编码只能包含字母、数字、下划线和中划线")
    @Schema(description = "板块编码")
    private String code;

    @Size(max = 500, message = "板块描述不能超过500个字符")
    @Schema(description = "板块描述")
    private String description;

    @Size(max = 512, message = "封面地址不能超过512个字符")
    @Schema(description = "封面地址")
    private String coverUrl;

    @Min(value = 0, message = "父级板块ID不能小于0")
    @Schema(description = "父级板块ID，0表示顶级板块")
    private Long parentId;

    @Min(value = 0, message = "排序值不能小于0")
    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态")
    private StatusEnum status;
}
