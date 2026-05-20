package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "租户信息")
public class TenantResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "租户ID")
    @Sortable(description = "租户ID")
    private Long id;

    @Schema(description = "租户名称")
    @Sortable(description = "租户名称")
    private String name;

    @Schema(description = "创建时间")
    @Sortable(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @Sortable(description = "更新时间")
    private LocalDateTime updatedAt;
}
