package me.link.bootstrap.interfaces.dto.request.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.io.Serial;
import java.io.Serializable;

@Data
@Schema(description = "更新用户请求")
public class UserUpdateRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "username")
    private String username;
    @Schema(description = "password")
    private String password;
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
}
