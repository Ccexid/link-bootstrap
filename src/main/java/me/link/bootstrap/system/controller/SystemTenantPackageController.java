package me.link.bootstrap.system.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.core.common.Result;
import me.link.bootstrap.core.common.SelectOptions;
import me.link.bootstrap.core.lock.annotation.Lock;
import me.link.bootstrap.core.log.annotation.Log;
import me.link.bootstrap.system.tenant.entity.SystemTenantPackage;
import me.link.bootstrap.system.tenant.service.ISystemTenantPackageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "02.租户套餐管理")
@RestController
@RequestMapping("/system/tenant-packages")
@RequiredArgsConstructor
public class SystemTenantPackageController {

    private final ISystemTenantPackageService packageService;

    @Operation(summary = "创建套餐")
    @PostMapping
    @Log(module = "套餐管理", operation = "'创建套餐: ' + #pkg.packageName")
    @Lock(key = "'tenant_package:create:' + #pkg.packageName")
    public Result<Long> createPackage(@RequestBody SystemTenantPackage pkg) {
        packageService.save(pkg);
        return Result.success(pkg.getId());
    }

    @Operation(summary = "修改套餐")
    @PutMapping("/{id}")
    @Log(module = "套餐管理", operation = "'修改套餐'", businessId = "#id", isDiff = true)
    @Lock(key = "'tenant_package:update:' + #id")
    public Result<Boolean> updatePackage(@PathVariable Long id, @RequestBody SystemTenantPackage pkg) {
        pkg.setId(id);
        return Result.success(packageService.updateById(pkg));
    }

    @Operation(summary = "获取详情")
    @GetMapping("/{id}")
    public Result<SystemTenantPackage> getPackage(@PathVariable Long id) {
        return Result.success(packageService.getById(id));
    }

    @Operation(summary = "分页查询套餐")
    @GetMapping("/page")
    @Log(module = "套餐管理", operation = "分页查询")
    public Result<IPage<SystemTenantPackage>> getPage(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String packageName) {
        return Result.success(packageService.getPackagePage(pageNo, pageSize, packageName));
    }

    @Operation(summary = "获取套餐下拉列表")
    @GetMapping("/select")
    public Result<List<SelectOptions>> getSelect() {
        return Result.success(packageService.getPackageSelect());
    }
}