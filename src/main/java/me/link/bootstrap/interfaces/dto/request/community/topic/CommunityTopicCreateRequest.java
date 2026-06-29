package me.link.bootstrap.interfaces.dto.request.community.topic;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;

/**
 * 创建社区话题请求。
 */
@Data
@Schema(description = "创建社区话题请求")
public class CommunityTopicCreateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "所属板块ID不能为空")
    @Min(value = 1, message = "所属板块ID必须大于0")
    @Schema(description = "所属板块ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sectionId;

    @NotBlank(message = "话题名称不能为空")
    @Size(max = 80, message = "话题名称不能超过80个字符")
    @Schema(description = "话题名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "话题编码不能为空")
    @Size(max = 80, message = "话题编码不能超过80个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_-]{2,80}$", message = "话题编码只能包含字母、数字、下划线和中划线")
    @Schema(description = "话题编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @Size(max = 500, message = "话题描述不能超过500个字符")
    @Schema(description = "话题描述")
    private String description;

    @Size(max = 512, message = "封面地址不能超过512个字符")
    @Schema(description = "封面地址")
    private String coverUrl;

    @Min(value = 0, message = "排序值不能小于0")
    @Schema(description = "排序值")
    private Integer sort;

    @Schema(description = "状态")
    private StatusEnum status;
}
