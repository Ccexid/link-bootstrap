package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateMenuCommand;
import me.link.bootstrap.application.command.MenuPageQuery;
import me.link.bootstrap.application.command.UpdateMenuCommand;
import me.link.bootstrap.application.service.MenuApplicationService;
import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.menu.MenuCreateRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuPageRequest;
import me.link.bootstrap.interfaces.dto.request.menu.MenuUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.MenuResponseVO;
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
@RequestMapping(GlobalConstants.API_PREFIX + "/system/menu")
@Validated
@RequiredArgsConstructor
@Tag(name = "菜单接口", description = "菜单增删改查接口")
public class MenuController {

    private final MenuApplicationService menuApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @PostMapping
    @SaCheckPermission("system:menu:create")
    @Operation(summary = "创建菜单", description = "创建菜单基础信息")
    public ResultResponse<MenuResponseVO> create(@Valid @RequestBody MenuCreateRequest request) {
        MenuEntity menu = menuApplicationService.create(new CreateMenuCommand(
                request.getName(),
                request.getPermission(),
                request.getType(),
                request.getSort(),
                request.getParentId(),
                request.getPath(),
                request.getIcon(),
                request.getComponent(),
                request.getComponentName(),
                request.getStatus(),
                request.getVisible(),
                request.getKeepAlive(),
                request.getAlwaysShow()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(menu));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询菜单详情", description = "根据ID查询菜单详情")
    public ResultResponse<MenuResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(menuApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询菜单", description = "分页查询菜单列表")
    public ResultTableResponse<MenuResponseVO> page(@Validated @SortWhitelist(MenuResponseVO.class) MenuPageRequest request) {
        PageResult<MenuEntity> pageResult = menuApplicationService.page(new MenuPageQuery(
                request.getPageNo(),
                request.getPageSize(),
                request.getName(),
                request.getPermission(),
                request.getType(),
                request.getParentId(),
                request.getStatus(),
                request.getSortingFields()
        ));
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("system:menu:update")
    @Operation(summary = "更新菜单", description = "更新菜单基础信息")
    public ResultResponse<MenuResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody MenuUpdateRequest request) {
        MenuEntity menu = menuApplicationService.update(new UpdateMenuCommand(
                id,
                request.getName(),
                request.getPermission(),
                request.getType(),
                request.getSort(),
                request.getParentId(),
                request.getPath(),
                request.getIcon(),
                request.getComponent(),
                request.getComponentName(),
                request.getStatus(),
                request.getVisible(),
                request.getKeepAlive(),
                request.getAlwaysShow()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(menu));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:menu:delete")
    @Operation(summary = "删除菜单", description = "根据ID删除菜单")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        menuApplicationService.delete(id);
        return ResultResponse.success();
    }

}
