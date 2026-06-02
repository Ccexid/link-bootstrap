package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.TenantPackageEntity;

import java.util.LinkedHashSet;
import java.util.Set;

public final class TenantPackageFactory {

    private static final int NAME_MAX_LENGTH = 30;
    private static final int REMARK_MAX_LENGTH = 256;

    private TenantPackageFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static TenantPackageEntity create(String name, String remark, Set<Long> menuIds) {
        return TenantPackageEntity.create(normalizeName(name), normalizeRemark(remark), normalizeMenuIds(menuIds));
    }

    public static void changeBasicInfo(TenantPackageEntity tenantPackage, String name, String remark, Set<Long> menuIds) {
        if (tenantPackage == null) {
            throw new IllegalArgumentException("租户套餐不能为空");
        }
        tenantPackage.changeBasicInfo(normalizeName(name), normalizeRemark(remark), normalizeMenuIds(menuIds));
    }

    private static String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("套餐名不能为空");
        }
        String normalizedName = name.trim();
        if (normalizedName.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("套餐名长度不能超过%d个字符", NAME_MAX_LENGTH));
        }
        return normalizedName;
    }

    private static String normalizeRemark(String remark) {
        if (remark == null || remark.trim().isEmpty()) {
            return "";
        }
        String normalizedRemark = remark.trim();
        if (normalizedRemark.length() > REMARK_MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("备注长度不能超过%d个字符", REMARK_MAX_LENGTH));
        }
        return normalizedRemark;
    }

    private static Set<Long> normalizeMenuIds(Set<Long> menuIds) {
        if (menuIds == null || menuIds.isEmpty()) {
            throw new IllegalArgumentException("关联菜单不能为空");
        }
        Set<Long> normalizedMenuIds = new LinkedHashSet<>();
        for (Long menuId : menuIds) {
            if (menuId == null || menuId <= 0) {
                throw new IllegalArgumentException("关联菜单编号必须大于0");
            }
            normalizedMenuIds.add(menuId);
        }
        return normalizedMenuIds;
    }
}
