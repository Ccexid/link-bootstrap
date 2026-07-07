package me.link.bootstrap.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.RoleService;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.interfaces.dto.request.role.RoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.role.RolePageRequest;
import me.link.bootstrap.interfaces.dto.request.role.RoleUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.RoleResponseVO;
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
 * 角色接口控制器，对外提供角色增删改查 REST 接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/roles")
@Validated
@RequiredArgsConstructor
@Tag(name = "角色接口", description = "角色增删改查接口")
public class RoleController {

    private final RoleService roleService;

    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    @Idempotent
    @Operation(summary = "创建角色", description = "创建角色基础信息")
    public ResultResponse<RoleResponseVO> create(@Valid @RequestBody RoleCreateRequest request) {
        return ResultResponse.success(roleService.create(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    @Operation(summary = "查询角色详情", description = "根据ID查询角色详情")
    public ResultResponse<RoleResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(roleService.get(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('system:role:list')")
    @Operation(summary = "分页查询角色", description = "分页查询角色列表")
    public ResultTableResponse<RoleResponseVO> page(@Validated @SortWhitelist(RoleResponseVO.class) RolePageRequest request) {
        PageResult<RoleResponseVO> pageResult = roleService.page(request);
        return ResultTableResponse.success(pageResult.records(), pageResult.total());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:update')")
    @Idempotent
    @Operation(summary = "更新角色", description = "更新角色基础信息")
    public ResultResponse<RoleResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody RoleUpdateRequest request) {
        return ResultResponse.success(roleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        roleService.delete(id);
        return ResultResponse.success();
    }

}
