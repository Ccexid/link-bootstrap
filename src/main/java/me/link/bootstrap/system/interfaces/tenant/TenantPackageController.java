package me.link.bootstrap.system.interfaces.tenant;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.core.common.Result;
import me.link.bootstrap.core.lock.annotation.Lock;
import me.link.bootstrap.core.log.annotation.Log;
import me.link.bootstrap.system.application.tenant.ITenantPackageService;
import me.link.bootstrap.system.domain.tenant.entity.TenantPackage;
import me.link.bootstrap.system.interfaces.tenant.vo.TenantPackagePageReqVO;
import org.springframework.web.bind.annotation.*;

@Tag(name = "01.租户套餐管理")
@RestController
@RequestMapping("/system/tenant-packages")
@RequiredArgsConstructor
public class TenantPackageController {

    private final ITenantPackageService packageService;

    @Operation(summary = "创建套餐")
    @PostMapping
    @Log(module = "套餐管理", operation = "创建套餐", isDiff = false)
    @Lock(key = "'tenant_package:create:' + #req.packageName")
    public Result<Long> create(@RequestBody TenantPackage req) {
        packageService.save(req);
        return Result.success(req.getId());
    }

    @Operation(summary = "更新套餐")
    @PutMapping("/{id}")
    @Log(module = "套餐管理", operation = "更新套餐", businessId = "#id")
    @Lock(key = "'tenant_package:update:' + #id")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody TenantPackage req) {
        req.setId(id);
        return Result.success(packageService.updateById(req));
    }

    @Operation(summary = "删除套餐")
    @DeleteMapping("/{id}")
    @Log(module = "套餐管理", operation = "删除套餐", businessId = "#id", isDiff = false)
    @Lock(key = "'tenant_package:delete:' + #id")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(packageService.removeById(id));
    }

    @Operation(summary = "获取套餐详情")
    @GetMapping("/{id}")
    @Log(module = "套餐管理", operation = "查看详情")
    public Result<TenantPackage> get(@PathVariable Long id) {
        return Result.success(packageService.getById(id));
    }

    @Operation(summary = "分页查询套餐")
    @GetMapping("/page")
    @Log(module = "套餐管理", operation = "分页查询")
    public Result<IPage<TenantPackage>> page(TenantPackagePageReqVO pageReqVO) {
        // 3. 执行查询并返回
        return Result.success(packageService.getPackagePage(pageReqVO));
    }
}