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
 * 组织响应 VO，定义接口返回给前端的组织字段。
 */
@Data
@Schema(description = "组织信息")
public class OrganizationResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "编号")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "名称")
    @Sortable(description = "name")
    private String name;
    @Schema(description = "组织类型")
    private Integer orgType;
    @Schema(description = "父级编号")
    private Long parentId;
    @Schema(description = "层级路径")
    private String ancestors;
    @Schema(description = "层级深度")
    private Integer level;
    @Schema(description = "负责人姓名")
    private String contactName;
    @Schema(description = "联系电话")
    private String contactMobile;
    @Schema(description = "状态")
    private StatusEnum status;
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
