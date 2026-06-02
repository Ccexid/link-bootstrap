package me.link.bootstrap.interfaces.dto.request.organization;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "组织分页查询请求")
public class OrganizationPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "name")
    private String name;

    @Schema(description = "orgType")
    private Integer orgType;

    @Schema(description = "parentId")
    private Long parentId;

    @Schema(description = "status")
    private StatusEnum status;
}
