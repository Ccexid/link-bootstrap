package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.util.regex.Pattern;

/**
 * 组织领域工厂，集中封装组织创建和变更时的业务规则校验。
 * <p>
 * 组织树属于租户内数据，应用层负责编排父子关系和权限边界，领域工厂负责兜底基础字段和租户编号。
 * </p>
 */
public final class OrganizationFactory {

    private static final Pattern CONTACT_MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private OrganizationFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static OrganizationEntity create(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        validate(name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
        return OrganizationEntity.create(name.trim(), orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
    }

    public static void changeBasicInfo(OrganizationEntity organization, String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        if (organization == null) {
            throw new IllegalArgumentException("组织不能为空");
        }
        validate(name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
        organization.changeBasicInfo(name.trim(), orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId);
    }

    private static void validate(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("组织name不能为空");
        }
        if (contactMobile != null && !contactMobile.trim().isEmpty()
                && !CONTACT_MOBILE_PATTERN.matcher(contactMobile.trim()).matches()) {
            throw new IllegalArgumentException("组织联系电话格式不正确");
        }
    }
}
