package me.link.bootstrap.interfaces.dto.request.community.section;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;

import java.io.Serial;

/**
 * 社区板块分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "社区板块分页查询请求")
public class CommunitySectionPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "板块名称")
    private String name;

    @Schema(description = "板块编码")
    private String code;

    @Schema(description = "父级板块ID")
    private Long parentId;

    @Schema(description = "状态")
    private StatusEnum status;
}
