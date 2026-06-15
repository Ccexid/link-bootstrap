package me.link.bootstrap.interfaces.dto.request.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

/**
 * 角色分页查询请求 DTO，承载角色筛选条件、分页参数和排序字段。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "角色分页查询请求")
public class RolePageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "name")
    private String name;

    @Schema(description = "code")
    private String code;

    @Schema(description = "status")
    private StatusEnum status;

    @Schema(description = "type")
    private Integer type;
}
