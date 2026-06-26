package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.OrganizationApplicationService;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationCreateRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationPageRequest;
import me.link.bootstrap.interfaces.dto.request.organization.OrganizationUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.OrganizationResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
import me.link.bootstrap.shared.kernel.annotation.Idempotent;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 组织接口控制器，对外提供组织增删改查 REST 接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/organization")
@Validated
@RequiredArgsConstructor
@Tag(name = "组织接口", description = "组织增删改查接口")
public class OrganizationController {

    private final OrganizationApplicationService organizationApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @PostMapping
    @SaCheckPermission("system:organization:create")
    @Idempotent
    @Operation(summary = "创建组织", description = "创建组织基础信息")
    public ResultResponse<OrganizationResponseVO> create(@Valid @RequestBody OrganizationCreateRequest request) {
        OrganizationPO organization = organizationApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(organization));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询组织详情", description = "根据ID查询组织详情")
    public ResultResponse<OrganizationResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(organizationApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询组织", description = "分页查询组织列表")
    public ResultTableResponse<OrganizationResponseVO> page(@Validated @SortWhitelist(OrganizationResponseVO.class) OrganizationPageRequest request) {
        PageResult<OrganizationPO> pageResult = organizationApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("system:organization:update")
    @Idempotent
    @Operation(summary = "更新组织", description = "更新组织基础信息")
    public ResultResponse<OrganizationResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody OrganizationUpdateRequest request) {
        OrganizationPO organization = organizationApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(organization));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:organization:delete")
    @Operation(summary = "删除组织", description = "根据ID删除组织")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        organizationApplicationService.delete(id);
        return ResultResponse.success();
    }

}
