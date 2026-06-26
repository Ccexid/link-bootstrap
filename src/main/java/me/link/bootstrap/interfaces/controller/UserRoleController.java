package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.UserRoleApplicationService;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleAssignRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRolePageRequest;
import me.link.bootstrap.interfaces.dto.request.userrole.UserRoleUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.UserRoleResponseVO;
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
 * 用户角色关联接口控制器，对外提供用户角色分配关系维护接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/user-role")
@Validated
@RequiredArgsConstructor
@Tag(name = "用户角色关联接口", description = "用户角色关联增删改查接口")
public class UserRoleController {

    private final UserRoleApplicationService userRoleApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @PostMapping
    @SaCheckPermission("system:user-role:create")
    @Idempotent
    @Operation(summary = "创建用户角色关联", description = "创建用户角色关联基础信息")
    public ResultResponse<UserRoleResponseVO> create(@Valid @RequestBody UserRoleCreateRequest request) {
        UserRolePO userRole = userRoleApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(userRole));
    }

    @PostMapping("/assign")
    @SaCheckPermission("system:user-role:assign")
    @Idempotent
    @Operation(summary = "批量分配用户角色", description = "覆盖指定用户的角色分配")
    public ResultResponse<Void> assign(@Valid @RequestBody UserRoleAssignRequest request) {
        userRoleApplicationService.assign(request);
        return ResultResponse.success();
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户角色关联详情", description = "根据ID查询用户角色关联详情")
    public ResultResponse<UserRoleResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(userRoleApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询用户角色关联", description = "分页查询用户角色关联列表")
    public ResultTableResponse<UserRoleResponseVO> page(@Validated @SortWhitelist(UserRoleResponseVO.class) UserRolePageRequest request) {
        PageResult<UserRolePO> pageResult = userRoleApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("system:user-role:update")
    @Idempotent
    @Operation(summary = "更新用户角色关联", description = "更新用户角色关联基础信息")
    public ResultResponse<UserRoleResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody UserRoleUpdateRequest request) {
        UserRolePO userRole = userRoleApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(userRole));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:user-role:delete")
    @Operation(summary = "删除用户角色关联", description = "根据ID删除用户角色关联")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        userRoleApplicationService.delete(id);
        return ResultResponse.success();
    }

}
