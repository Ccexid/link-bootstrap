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
 * 角色响应 VO，定义接口返回给前端的角色字段。
 */
@Data
@Schema(description = "角色信息")
public class RoleResponseVO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "编号")
    @Sortable(description = "id")
    private Long id;
    @Schema(description = "名称")
    @Sortable(description = "name")
    private String name;
    @Schema(description = "编码")
    @Sortable(description = "code")
    private String code;
    @Schema(description = "显示顺序")
    @Sortable(description = "sort")
    private Integer sort;
    @Schema(description = "数据范围")
    private Integer dataScope;
    @Schema(description = "数据范围组织编号数组")
    private String dataScopeDeptIds;
    @Schema(description = "状态")
    private StatusEnum status;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "备注")
    private String remark;
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
