package me.link.bootstrap.interfaces.dto.request.user;

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
@Schema(description = "用户分页查询请求")
public class UserPageRequest extends SortablePageRequest {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "username")
    private String username;

    @Schema(description = "nickname")
    private String nickname;

    @Schema(description = "mobile")
    private String mobile;

    @Schema(description = "userType")
    private Integer userType;

    @Schema(description = "status")
    private StatusEnum status;

    @Schema(description = "tenantId")
    private Long tenantId;
}
