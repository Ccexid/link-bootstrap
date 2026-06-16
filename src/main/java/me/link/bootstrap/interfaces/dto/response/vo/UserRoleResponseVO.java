package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户角色关联响应 VO，定义接口返回给前端的分配关系字段。
 */
@Data
@Schema(description = "用户角色关联信息")
public class UserRoleResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "编号")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "用户编号")
    @Sortable(description = "userId")
    private Long userId;
    @Schema(description = "角色编号")
    @Sortable(description = "roleId")
    private Long roleId;
    @Schema(description = "租户编号")
    @Sortable(description = "tenantId")
    private Long tenantId;
    @Schema(description = "创建时间")
    @Sortable(description = "createdAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @Schema(description = "更新时间")
    @Sortable(description = "updatedAt")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
