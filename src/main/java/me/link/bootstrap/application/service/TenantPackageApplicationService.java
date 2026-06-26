package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.infrastructure.persistence.internal.TenantPackageInternalService;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageCreateRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackagePageRequest;
import me.link.bootstrap.interfaces.dto.request.tenantpackage.TenantPackageUpdateRequest;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * 租户套餐服务，按轻量 CRUD 结构直接编排校验、事务和持久化。
 */
@Service
@RequiredArgsConstructor
public class TenantPackageApplicationService {

    private static final int NAME_MAX_LENGTH = 30;
    private static final int REMARK_MAX_LENGTH = 256;
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "name", "name",
            "created_at", "create_time",
            "updated_at", "update_time"
    );

    private final TenantPackageInternalService tenantPackageInternalService;

    @Transactional
    public TenantPackagePO create(TenantPackageCreateRequest request) {
        TenantPackagePO tenantPackage = new TenantPackagePO();
        tenantPackage.setName(normalizeName(request.getName()));
        tenantPackage.setRemark(normalizeRemark(request.getRemark()));
        tenantPackage.setMenuIds(normalizeMenuIds(request.getMenuIds()));
        tenantPackage.setStatus(StatusEnum.NORMAL);
        tenantPackageInternalService.save(tenantPackage);
        return tenantPackage;
    }

    public TenantPackagePO get(Long id) {
        return ApplicationAssert.requireFound(tenantPackageInternalService.getById(id), ErrorCode.TENANT_PACKAGE_NOT_FOUND);
    }

    public PageResult<TenantPackagePO> page(TenantPackagePageRequest request) {
        Page<TenantPackagePO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<TenantPackagePO> wrapper = new LambdaQueryWrapper<TenantPackagePO>()
                .like(StrUtil.isNotBlank(request.getName()), TenantPackagePO::getName, request.getName())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), TenantPackagePO::getId);
        Page<TenantPackagePO> result = tenantPackageInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    @Transactional
    public TenantPackagePO update(Long id, TenantPackageUpdateRequest request) {
        TenantPackagePO tenantPackage = get(id);
        tenantPackage.setName(normalizeName(request.getName()));
        tenantPackage.setRemark(normalizeRemark(request.getRemark()));
        tenantPackage.setMenuIds(normalizeMenuIds(request.getMenuIds()));
        if (Boolean.TRUE.equals(request.getEnabled())) {
            tenantPackage.setStatus(StatusEnum.NORMAL);
        }
        if (Boolean.FALSE.equals(request.getEnabled())) {
            tenantPackage.setStatus(StatusEnum.DISABLE);
        }
        ApplicationAssert.requireSuccess(tenantPackageInternalService.updateById(tenantPackage), ErrorCode.TENANT_PACKAGE_NOT_FOUND);
        return get(id);
    }

    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(tenantPackageInternalService.removeById(id), ErrorCode.TENANT_PACKAGE_NOT_FOUND);
    }

    private static String normalizeName(String name) {
        if (StrUtil.isBlank(name)) {
            ApplicationAssert.invalidParam("套餐名不能为空");
        }
        String normalizedName = name.trim();
        if (normalizedName.length() > NAME_MAX_LENGTH) {
            ApplicationAssert.invalidParam(String.format("套餐名长度不能超过%d个字符", NAME_MAX_LENGTH));
        }
        return normalizedName;
    }

    private static String normalizeRemark(String remark) {
        if (StrUtil.isBlank(remark)) {
            return "";
        }
        String normalizedRemark = remark.trim();
        if (normalizedRemark.length() > REMARK_MAX_LENGTH) {
            ApplicationAssert.invalidParam(String.format("备注长度不能超过%d个字符", REMARK_MAX_LENGTH));
        }
        return normalizedRemark;
    }

    private static Set<Long> normalizeMenuIds(Set<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            ApplicationAssert.invalidParam("关联菜单不能为空");
        }
        Set<Long> normalizedMenuIds = new LinkedHashSet<>();
        for (Long menuId : menuIds) {
            if (menuId == null || menuId <= 0) {
                ApplicationAssert.invalidParam("关联菜单编号必须大于0");
            }
            normalizedMenuIds.add(menuId);
        }
        return normalizedMenuIds;
    }
}
