package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateRoleCommand;
import me.link.bootstrap.application.command.RolePageQuery;
import me.link.bootstrap.application.command.UpdateRoleCommand;
import me.link.bootstrap.application.service.RoleApplicationService;
import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.role.RoleCreateRequest;
import me.link.bootstrap.interfaces.dto.request.role.RolePageRequest;
import me.link.bootstrap.interfaces.dto.request.role.RoleUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.RoleResponseVO;
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

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/role")
@Validated
@RequiredArgsConstructor
@Tag(name = "角色接口", description = "角色增删改查接口")
public class RoleController {

    private final RoleApplicationService roleApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @PostMapping
    @SaCheckPermission("system:role:create")
    @Operation(summary = "创建角色", description = "创建角色基础信息")
    public ResultResponse<RoleResponseVO> create(@Valid @RequestBody RoleCreateRequest request) {
        RoleEntity role = roleApplicationService.create(new CreateRoleCommand(
                request.getName(),
                request.getCode(),
                request.getSort(),
                request.getDataScope(),
                request.getDataScopeDeptIds(),
                request.getStatus(),
                request.getType(),
                request.getRemark()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(role));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询角色详情", description = "根据ID查询角色详情")
    public ResultResponse<RoleResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(roleApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询角色", description = "分页查询角色列表")
    public ResultTableResponse<RoleResponseVO> page(@Validated @SortWhitelist(RoleResponseVO.class) RolePageRequest request) {
        PageResult<RoleEntity> pageResult = roleApplicationService.page(new RolePageQuery(
                request.getPageNo(),
                request.getPageSize(),
                request.getName(),
                request.getCode(),
                request.getStatus(),
                request.getType(),
                request.getSortingFields()
        ));
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("system:role:update")
    @Operation(summary = "更新角色", description = "更新角色基础信息")
    public ResultResponse<RoleResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody RoleUpdateRequest request) {
        RoleEntity role = roleApplicationService.update(new UpdateRoleCommand(
                id,
                request.getName(),
                request.getCode(),
                request.getSort(),
                request.getDataScope(),
                request.getDataScopeDeptIds(),
                request.getStatus(),
                request.getType(),
                request.getRemark()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(role));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:role:delete")
    @Operation(summary = "删除角色", description = "根据ID删除角色")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        roleApplicationService.delete(id);
        return ResultResponse.success();
    }

}
