package me.link.bootstrap.interfaces.dto.request.tenantpackage;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.link.bootstrap.interfaces.dto.request.SortablePageRequest;

import java.io.Serial;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "租户套餐分页查询请求")
public class TenantPackagePageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "套餐名")
    private String name;
}
