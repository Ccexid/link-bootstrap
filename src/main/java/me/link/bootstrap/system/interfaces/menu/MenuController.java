package me.link.bootstrap.system.interfaces.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.core.common.Result;
import me.link.bootstrap.core.lock.annotation.Lock;
import me.link.bootstrap.core.log.annotation.Log;
import me.link.bootstrap.system.application.menu.IMenuService;
import me.link.bootstrap.system.domain.menu.entity.Menu;
import me.link.bootstrap.system.interfaces.menu.vo.MenuPageReqVO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "03.菜单管理")
@RestController
@RequestMapping("/system/menus")
@RequiredArgsConstructor
public class MenuController {

    private final IMenuService menuService;

    @Operation(summary = "创建菜单")
    @PostMapping
    @Log(module = "菜单管理", operation = "创建菜单", isDiff = false)
    @Lock(key = "'menu:create:' + #req.menuName")
    public Result<Long> create(@RequestBody Menu req) {
        menuService.save(req);
        return Result.success(req.getId());
    }

    @Operation(summary = "更新菜单")
    @PutMapping("/{id}")
    @Log(module = "菜单管理", operation = "更新菜单", businessId = "#id")
    @Lock(key = "'menu:update:' + #id")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Menu req) {
        req.setId(id);
        return Result.success(menuService.updateById(req));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @Log(module = "菜单管理", operation = "删除菜单", businessId = "#id", isDiff = false)
    @Lock(key = "'menu:delete:' + #id")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(menuService.removeById(id));
    }

    @Operation(summary = "获取菜单列表(树形结构)")
    @GetMapping
    @Log(module = "菜单管理", operation = "查询菜单树")
    public Result<List<Menu>> list(MenuPageReqVO req) {
        LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<Menu>()
                .like(StringUtils.hasText(req.getMenuName()), Menu::getMenuName, req.getMenuName())
                .eq(req.getStatus() != null, Menu::getStatus, req.getStatus())
                .orderByAsc(Menu::getOrderNum);
        return Result.success(menuService.list(wrapper));
    }
}