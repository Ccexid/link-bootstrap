package me.link.bootstrap.interfaces.dto.request.community.comment;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

/**
 * 社区评论分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区评论分页查询请求")
public class CommunityCommentPageRequest extends SortablePageRequest {

    @Min(value = 1, message = "帖子ID必须大于0")
    @Schema(description = "帖子ID")
    private Long postId;

    @Min(value = 0, message = "根评论ID不能小于0")
    @Schema(description = "根评论ID，0表示查询一级评论")
    private Long rootId;

    @Min(value = 1, message = "作者ID必须大于0")
    @Schema(description = "作者ID")
    private Long authorId;

    @Schema(description = "状态")
    private StatusEnum status;
}
