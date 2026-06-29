package me.link.bootstrap.interfaces.dto.request.community.post;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;

/**
 * 社区帖子分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区帖子分页查询请求")
public class CommunityPostPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "所属板块ID")
    private Long sectionId;

    @Schema(description = "所属话题ID")
    private Long topicId;

    @Schema(description = "作者用户ID")
    private Long authorId;

    @Schema(description = "标题关键词")
    private String title;

    @Schema(description = "状态")
    private StatusEnum status;
}
