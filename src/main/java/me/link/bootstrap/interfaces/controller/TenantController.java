package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.TenantApplicationService;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantPageRequest;
import me.link.bootstrap.interfaces.dto.request.tenant.TenantUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.TenantResponseVO;
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
 * 租户接口控制器，对外提供租户增删改查 REST 接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/tenant")
@Validated
@RequiredArgsConstructor
@Tag(name = "租户管理接口", description = "租户增删改查接口")
public class TenantController {

    private final TenantApplicationService tenantApplicationService;
    private final ResponseVOConverter responseVOConverter;

    /**
     * 创建业务对象。
     */
    @PostMapping
    @SaCheckPermission("system:tenant:create")
    @Idempotent
    @Operation(summary = "创建租户", description = "创建租户基础信息")
    public ResultResponse<TenantResponseVO> create(@Valid @RequestBody TenantCreateRequest request) {
        TenantPO tenant = tenantApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(tenant));
    }

    /**
     * 根据主键查询业务对象详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询租户详情", description = "根据租户ID查询租户详情")
    public ResultResponse<TenantResponseVO> get(@PathVariable @NotNull(message = "租户ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(tenantApplicationService.get(id)));
    }

    /**
     * 分页查询业务对象列表。
     */
    @GetMapping
    @Operation(summary = "分页查询租户", description = "分页查询租户列表")
    public ResultTableResponse<TenantResponseVO> page(@Validated @SortWhitelist(TenantResponseVO.class) TenantPageRequest request) {
        PageResult<TenantPO> pageResult = tenantApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    /**
     * 更新业务对象。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:tenant:update")
    @Idempotent
    @Operation(summary = "更新租户", description = "更新租户基础信息")
    public ResultResponse<TenantResponseVO> update(@PathVariable @NotNull(message = "租户ID不能为空") Long id,
                                                   @Valid @RequestBody TenantUpdateRequest request) {
        TenantPO tenant = tenantApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(tenant));
    }

    /**
     * 根据主键删除业务对象。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:tenant:delete")
    @Operation(summary = "删除租户", description = "根据租户ID删除租户")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "租户ID不能为空") Long id) {
        tenantApplicationService.delete(id);
        return ResultResponse.success();
    }

}
