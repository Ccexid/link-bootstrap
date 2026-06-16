package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

/**
 * 角色领域工厂，集中封装角色创建和变更时的业务规则校验。
 */
public final class RoleFactory {

    private RoleFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static RoleEntity create(String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId) {
        validate(name, code, sort, dataScope, dataScopeDeptIds, status, type, remark, tenantId);
        return RoleEntity.create(name, code, sort, dataScope, dataScopeDeptIds, status, type, remark, tenantId);
    }

    public static void changeBasicInfo(RoleEntity role, String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId) {
        if (role == null) {
            throw new IllegalArgumentException("角色不能为空");
        }
        validate(name, code, sort, dataScope, dataScopeDeptIds, status, type, remark, tenantId);
        role.changeBasicInfo(name, code, sort, dataScope, dataScopeDeptIds, status, type, remark, tenantId);
    }

    private static void validate(String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("角色name不能为空");
        }
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("角色code不能为空");
        }
    }
}
