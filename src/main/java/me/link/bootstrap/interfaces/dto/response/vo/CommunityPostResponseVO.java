package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.shared.kernel.annotation.Sortable;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 社区帖子响应 VO。
 */
@Data
@Schema(description = "社区帖子响应")
public class CommunityPostResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Sortable("id")
    @Schema(description = "帖子ID")
    private Long id;

    @Sortable("section_id")
    @Schema(description = "所属板块ID")
    private Long sectionId;

    @Sortable("topic_id")
    @Schema(description = "所属话题ID")
    private Long topicId;

    @Sortable("author_id")
    @Schema(description = "作者用户ID")
    private Long authorId;

    @Schema(description = "帖子标题")
    private String title;

    @Schema(description = "帖子摘要")
    private String summary;

    @Schema(description = "帖子正文")
    private String content;

    @Schema(description = "封面地址")
    private String coverUrl;

    @Sortable("view_count")
    @Schema(description = "浏览数")
    private Long viewCount;

    @Sortable("like_count")
    @Schema(description = "点赞数")
    private Long likeCount;

    @Sortable("comment_count")
    @Schema(description = "评论数")
    private Long commentCount;

    @Sortable("collect_count")
    @Schema(description = "收藏数")
    private Long collectCount;

    @Sortable("pinned")
    @Schema(description = "是否置顶")
    private Boolean pinned;

    @Sortable("featured")
    @Schema(description = "是否加精")
    private Boolean featured;

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
