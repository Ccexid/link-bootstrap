package me.link.bootstrap.interfaces.dto.request.community.topic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;

/**
 * 社区话题分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区话题分页查询请求")
public class CommunityTopicPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "所属板块ID")
    private Long sectionId;

    @Schema(description = "话题名称")
    private String name;

    @Schema(description = "话题编码")
    private String code;

    @Schema(description = "状态")
    private StatusEnum status;
}
