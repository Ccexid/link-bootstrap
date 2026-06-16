package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

/**
 * 组织领域工厂，集中封装组织创建和变更时的业务规则校验。
 */
public final class OrganizationFactory {

    private OrganizationFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static OrganizationEntity create(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        validate(name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
        return OrganizationEntity.create(name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
    }

    public static void changeBasicInfo(OrganizationEntity organization, String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        if (organization == null) {
            throw new IllegalArgumentException("组织不能为空");
        }
        validate(name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
        organization.changeBasicInfo(name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
    }

    private static void validate(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("组织name不能为空");
        }
    }
}
