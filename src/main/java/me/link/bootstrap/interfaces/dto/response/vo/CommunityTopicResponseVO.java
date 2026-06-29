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
 * 社区话题响应 VO。
 */
@Data
@Schema(description = "社区话题响应")
public class CommunityTopicResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Sortable("id")
    @Schema(description = "话题ID")
    private Long id;

    @Sortable("section_id")
    @Schema(description = "所属板块ID")
    private Long sectionId;

    @Sortable("name")
    @Schema(description = "话题名称")
    private String name;

    @Sortable("code")
    @Schema(description = "话题编码")
    private String code;

    @Schema(description = "话题描述")
    private String description;

    @Schema(description = "封面地址")
    private String coverUrl;

    @Sortable("sort")
    @Schema(description = "排序值")
    private Integer sort;

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
