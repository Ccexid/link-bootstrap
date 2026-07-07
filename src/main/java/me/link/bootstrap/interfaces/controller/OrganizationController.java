package me.link.bootstrap.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.OrganizationService;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
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
@RequestMapping(GlobalConstants.API_PREFIX + "/system/organizations")
@Validated
@RequiredArgsConstructor
@Tag(name = "组织接口", description = "组织增删改查接口")
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping
    @PreAuthorize("hasAuthority('system:organization:create')")
    @Idempotent
    @Operation(summary = "创建组织", description = "创建组织基础信息")
    public ResultResponse<OrganizationResponseVO> create(@Valid @RequestBody OrganizationCreateRequest request) {
        return ResultResponse.success(organizationService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:organization:query')")
    @Operation(summary = "查询组织详情", description = "根据ID查询组织详情")
    public ResultResponse<OrganizationResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(organizationService.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:organization:list')")
    @Operation(summary = "分页查询组织", description = "分页查询组织列表")
    public ResultTableResponse<OrganizationResponseVO> page(@Validated @SortWhitelist(OrganizationResponseVO.class) OrganizationPageRequest request) {
        PageResult<OrganizationResponseVO> pageResult = organizationService.page(request);
        return ResultTableResponse.success(pageResult.records(), pageResult.total());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:organization:update')")
    @Idempotent
    @Operation(summary = "更新组织", description = "更新组织基础信息")
    public ResultResponse<OrganizationResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody OrganizationUpdateRequest request) {
        return ResultResponse.success(organizationService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:organization:delete')")
    @Operation(summary = "删除组织", description = "根据ID删除组织")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        organizationService.delete(id);
        return ResultResponse.success();
    }

}
