package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateTenantPackageCommand;
import me.link.bootstrap.application.command.TenantPackagePageQuery;
import me.link.bootstrap.application.command.UpdateTenantPackageCommand;
import me.link.bootstrap.application.service.TenantPackageApplicationService;
import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackagePageRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.TenantPackageResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
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
 * 租户套餐接口控制器，对外提供套餐增删改查 REST 接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/tenant/package")
@Validated
@RequiredArgsConstructor
@Tag(name = "租户套餐接口", description = "租户套餐增删改查接口")
public class TenantPackageController {

    private final TenantPackageApplicationService tenantPackageApplicationService;
    private final ResponseVOConverter responseVOConverter;

    /**
     * 创建业务对象。
     */
    @PostMapping
    @SaCheckPermission("system:tenant-package:create")
    @Operation(summary = "创建租户套餐", description = "创建租户套餐基础信息")
    public ResultResponse<TenantPackageResponseVO> create(@Valid @RequestBody TenantPackageCreateRequest request) {
        TenantPackageEntity tenantPackage = tenantPackageApplicationService.create(new CreateTenantPackageCommand(
                request.getName(),
                request.getRemark(),
                request.getMenuIds()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(tenantPackage));
    }

    /**
     * 根据主键查询业务对象详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询租户套餐详情", description = "根据租户套餐ID查询租户套餐详情")
    public ResultResponse<TenantPackageResponseVO> get(@PathVariable @NotNull(message = "租户套餐ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(tenantPackageApplicationService.get(id)));
    }

    /**
     * 分页查询业务对象列表。
     */
    @GetMapping
    @Operation(summary = "分页查询租户套餐", description = "分页查询租户套餐列表")
    public ResultTableResponse<TenantPackageResponseVO> page(@Validated @SortWhitelist(TenantPackageResponseVO.class) TenantPackagePageRequest request) {
        PageResult<TenantPackageEntity> pageResult = tenantPackageApplicationService.page(new TenantPackagePageQuery(
                request.getPageNo(),
                request.getPageSize(),
                request.getName(),
                request.getSortingFields()
        ));
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    /**
     * 更新业务对象。
     */
    @PutMapping("/{id}")
    @SaCheckPermission("system:tenant-package:update")
    @Operation(summary = "更新租户套餐", description = "更新租户套餐基础信息")
    public ResultResponse<TenantPackageResponseVO> update(@PathVariable @NotNull(message = "租户套餐ID不能为空") Long id,
                                                          @Valid @RequestBody TenantPackageUpdateRequest request) {
        TenantPackageEntity tenantPackage = tenantPackageApplicationService.update(new UpdateTenantPackageCommand(
                id,
                request.getName(),
                request.getRemark(),
                request.getMenuIds(),
                request.getEnabled()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(tenantPackage));
    }

    /**
     * 根据主键删除业务对象。
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("system:tenant-package:delete")
    @Operation(summary = "删除租户套餐", description = "根据租户套餐ID删除租户套餐")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "租户套餐ID不能为空") Long id) {
        tenantPackageApplicationService.delete(id);
        return ResultResponse.success();
    }

}
