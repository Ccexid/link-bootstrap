package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "租户套餐信息")
public class TenantPackageResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "套餐编号")
    @Sortable(description = "套餐编号")
    private Long id;

    @Schema(description = "套餐名")
    @Sortable(description = "套餐名")
    private String name;

    @Schema(description = "状态")
    private StatusEnum status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "关联的菜单编号数组")
    private Set<Long> menuIds;

    @Schema(description = "创建时间")
    @Sortable(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @Sortable(description = "更新时间")
    private LocalDateTime updatedAt;
}
