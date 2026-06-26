package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.shared.kernel.annotation.Sortable;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 社区板块响应 VO。
 */
@Data
@Schema(description = "社区板块响应")
public class CommunitySectionResponseVO {

    @Sortable("id")
    @Schema(description = "板块ID")
    private Long id;

    @Sortable("name")
    @Schema(description = "板块名称")
    private String name;

    @Sortable("code")
    @Schema(description = "板块编码")
    private String code;

    @Schema(description = "板块描述")
    private String description;

    @Schema(description = "封面地址")
    private String coverUrl;

    @Sortable("parent_id")
    @Schema(description = "父级板块ID")
    private Long parentId;

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
