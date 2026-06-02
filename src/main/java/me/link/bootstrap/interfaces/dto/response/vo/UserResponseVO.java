package me.link.bootstrap.interfaces.dto.response.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Schema(description = "用户信息")
public class UserResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "id")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "username")
    @Sortable(description = "username")
    private String username;
    @Schema(description = "nickname")
    private String nickname;
    @Schema(description = "userType")
    private Integer userType;
    @Schema(description = "mobile")
    private String mobile;
    @Schema(description = "avatar")
    private String avatar;
    @Schema(description = "status")
    private StatusEnum status;
    @Schema(description = "orgId")
    private Long orgId;
    @Schema(description = "deptId")
    private Long deptId;
    @Schema(description = "loginIp")
    private String loginIp;
    @Schema(description = "loginDate")
    private LocalDateTime loginDate;
    @Schema(description = "tenantId")
    @Sortable(description = "tenantId")
    private Long tenantId;
    @Schema(description = "createdAt")
    @Sortable(description = "createdAt")
    private LocalDateTime createdAt;
    @Schema(description = "updatedAt")
    @Sortable(description = "updatedAt")
    private LocalDateTime updatedAt;
}
