package me.link.bootstrap.system.interfaces.tenant;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.core.common.Result;
import me.link.bootstrap.core.lock.annotation.Lock;
import me.link.bootstrap.core.log.annotation.Log;
import me.link.bootstrap.system.application.tenant.ITenantService;
import me.link.bootstrap.system.domain.tenant.entity.Tenant;
import me.link.bootstrap.system.interfaces.tenant.vo.TenantPageReqVO;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "02.租户管理")
@RestController
@RequestMapping("/system/tenants")
@RequiredArgsConstructor
public class TenantController {

    private final ITenantService tenantService;

    @Operation(summary = "创建租户")
    @PostMapping
    @Log(module = "租户管理", operation = "创建租户", isDiff = false)
    @Lock(key = "'tenant:create:' + #tenant.tenantName")
    public Result<Long> create(@RequestBody Tenant tenant) {
        return Result.success(tenantService.createTenant(tenant));
    }

    @Operation(summary = "更新租户")
    @PutMapping("/{id}")
    @Log(module = "租户管理", operation = "更新租户", businessId = "#id")
    @Lock(key = "'tenant:update:' + #id")
    public Result<Boolean> update(@PathVariable Long id, @RequestBody Tenant tenant) {
        tenant.setId(id);
        return Result.success(tenantService.updateById(tenant));
    }

    @Operation(summary = "删除租户")
    @DeleteMapping("/{id}")
    @Log(module = "租户管理", operation = "删除租户", businessId = "#id", isDiff = false)
    @Lock(key = "'tenant:delete:' + #id")
    public Result<Boolean> delete(@PathVariable Long id) {
        return Result.success(tenantService.removeById(id));
    }

    @Operation(summary = "获取租户详情")
    @GetMapping("/{id}")
    @Log(module = "租户管理", operation = "查询详情")
    public Result<Tenant> get(@PathVariable Long id) {
        return Result.success(tenantService.getById(id));
    }

    @Operation(summary = "分页查询租户")
    @GetMapping("/page")
    @Log(module = "租户管理", operation = "分页查询")
    public Result<IPage<Tenant>> page(TenantPageReqVO pageReqVO) {
        Page<Tenant> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        LambdaQueryWrapper<Tenant> wrapper = new LambdaQueryWrapper<Tenant>()
                .like(StringUtils.hasText(pageReqVO.getTenantName()), Tenant::getTenantName, pageReqVO.getTenantName())
                .eq(StringUtils.hasText(pageReqVO.getTenantType()), Tenant::getTenantType, pageReqVO.getTenantType())
                .eq(pageReqVO.getStatus() != null, Tenant::getStatus, pageReqVO.getStatus())
                .orderByDesc(Tenant::getId);

        return Result.success(tenantService.page(page, wrapper));
    }
}