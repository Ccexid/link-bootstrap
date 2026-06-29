package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.shared.kernel.annotation.Sortable;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社区评论响应。
 */
@Data
@Schema(description = "社区评论响应")
public class CommunityCommentResponseVO implements Serializable {

    @Sortable("id")
    @Schema(description = "评论ID")
    private Long id;

    @Sortable("post_id")
    @Schema(description = "帖子ID")
    private Long postId;

    @Sortable("parent_id")
    @Schema(description = "父评论ID")
    private Long parentId;

    @Sortable("root_id")
    @Schema(description = "根评论ID")
    private Long rootId;

    @Sortable("author_id")
    @Schema(description = "作者用户ID")
    private Long authorId;

    @Schema(description = "被回复用户ID")
    private Long replyToId;

    @Schema(description = "评论内容")
    private String content;

    @Sortable("like_count")
    @Schema(description = "点赞数")
    private Long likeCount;

    @Sortable("reply_count")
    @Schema(description = "回复数")
    private Long replyCount;

    @Schema(description = "状态")
    private StatusEnum status;

    @Sortable("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Sortable("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
