package me.link.bootstrap.interfaces.dto.request.community.post;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 更新社区帖子请求。
 */
@Data
@Schema(description = "更新社区帖子请求")
public class CommunityPostUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "所属板块ID不能为空")
    @Min(value = 1, message = "所属板块ID必须大于0")
    @Schema(description = "所属板块ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long sectionId;

    @Min(value = 1, message = "所属话题ID必须大于0")
    @Schema(description = "所属话题ID")
    private Long topicId;

    @NotBlank(message = "帖子标题不能为空")
    @Size(max = 120, message = "帖子标题不能超过120个字符")
    @Schema(description = "帖子标题", requiredMode = Schema.RequiredMode.REQUIRED)
    private String title;

    @Size(max = 300, message = "帖子摘要不能超过300个字符")
    @Schema(description = "帖子摘要")
    private String summary;

    @NotBlank(message = "帖子正文不能为空")
    @Size(max = 20000, message = "帖子正文不能超过20000个字符")
    @Schema(description = "帖子正文", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;

    @Size(max = 512, message = "封面地址不能超过512个字符")
    @Schema(description = "封面地址")
    private String coverUrl;
}
