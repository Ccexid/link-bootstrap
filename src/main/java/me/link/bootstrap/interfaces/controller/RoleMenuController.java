package me.link.bootstrap.interfaces.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.RoleMenuService;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuAuthorizeRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.rolemenu.RoleMenuUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.RoleMenuResponseVO;
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
 * 角色菜单关联接口控制器，对外提供角色菜单授权关系维护接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/role-menus")
@Validated
@RequiredArgsConstructor
@Tag(name = "角色菜单关联接口", description = "角色菜单关联增删改查接口")
public class RoleMenuController {

    private final RoleMenuService roleMenuService;

    /**
     * 创建角色菜单关联。
     */
    @PostMapping
    @PreAuthorize("hasAuthority('system:role-menu:create')")
    @Idempotent
    @Operation(summary = "创建角色菜单关联", description = "创建角色菜单关联基础信息")
    public ResultResponse<RoleMenuResponseVO> create(@Valid @RequestBody RoleMenuCreateRequest request) {
        return ResultResponse.success(roleMenuService.create(request));
    }

    /**
     * 批量授权角色菜单。
     */
    @PostMapping("/authorize")
    @PreAuthorize("hasAuthority('system:role-menu:authorize')")
    @Idempotent
    @Operation(summary = "批量授权角色菜单", description = "覆盖指定角色的菜单授权")
    public ResultResponse<Void> authorize(@Valid @RequestBody RoleMenuAuthorizeRequest request) {
        roleMenuService.authorize(request);
        return ResultResponse.success();
    }

    /**
     * 查询角色菜单关联详情。
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role-menu:query')")
    @Operation(summary = "查询角色菜单关联详情", description = "根据ID查询角色菜单关联详情")
    public ResultResponse<RoleMenuResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(roleMenuService.get(id));
    }

    /**
     * 分页查询角色菜单关联。
     */
    @GetMapping
    @PreAuthorize("hasAuthority('system:role-menu:list')")
    @Operation(summary = "分页查询角色菜单关联", description = "分页查询角色菜单关联列表")
    public ResultTableResponse<RoleMenuResponseVO> page(@Validated @SortWhitelist(RoleMenuResponseVO.class) RoleMenuPageRequest request) {
        PageResult<RoleMenuResponseVO> pageResult = roleMenuService.page(request);
        return ResultTableResponse.success(pageResult.records(), pageResult.total());
    }

    /**
     * 更新角色菜单关联。
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role-menu:update')")
    @Idempotent
    @Operation(summary = "更新角色菜单关联", description = "更新角色菜单关联基础信息")
    public ResultResponse<RoleMenuResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody RoleMenuUpdateRequest request) {
        return ResultResponse.success(roleMenuService.update(id, request));
    }

    /**
     * 删除角色菜单关联。
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role-menu:delete')")
    @Operation(summary = "删除角色菜单关联", description = "根据ID删除角色菜单关联")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        roleMenuService.delete(id);
        return ResultResponse.success();
    }

}
