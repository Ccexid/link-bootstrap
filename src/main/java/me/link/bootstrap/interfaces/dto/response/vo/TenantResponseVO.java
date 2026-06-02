package me.link.bootstrap.interfaces.dto.response.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.link.bootstrap.domain.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.annotation.Sortable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 租户响应视图对象，定义接口返回给前端的租户字段。
 */
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

    @Schema(description = "联系人用户编号")
    private Long contactUserId;

    @Schema(description = "联系人姓名")
    private String contactName;

    @Schema(description = "联系手机")
    private String contactMobile;

    @Schema(description = "租户状态")
    private StatusEnum status;

    @Schema(description = "绑定域名数组")
    private Set<String> websites;

    @Schema(description = "租户套餐编号")
    private Long packageId;

    @Schema(description = "过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "账号数量")
    private Integer accountCount;

    @Schema(description = "创建时间")
    @Sortable(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    @Sortable(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
