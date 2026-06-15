package me.link.bootstrap.domain.entity;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 组织领域实体，封装组织层级、联系人和租户归属信息。
 */
public class OrganizationEntity {

    private Long id;

    private String name;

    private Integer orgType;

    private Long parentId;

    private String ancestors;

    private Integer level;

    private String contactName;

    private String contactMobile;

    private StatusEnum status;

    private Long tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private OrganizationEntity(Long id, String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.orgType = orgType;
        this.parentId = parentId;
        this.ancestors = ancestors;
        this.level = level;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
        this.status = status;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static OrganizationEntity create(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        return new OrganizationEntity(null, name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId, null, null);
    }

    public static OrganizationEntity restore(Long id, String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new OrganizationEntity(id, name, orgType, parentId, ancestors, level, contactName, contactMobile, status, tenantId, createdAt, updatedAt);
    }

    public void changeBasicInfo(String name, Integer orgType, Long parentId, String ancestors, Integer level, String contactName, String contactMobile, StatusEnum status, Long tenantId) {
        this.name = name;
        this.orgType = orgType;
        this.parentId = parentId;
        this.ancestors = ancestors;
        this.level = level;
        this.contactName = contactName;
        this.contactMobile = contactMobile;
        this.status = status;
        this.tenantId = tenantId;
    }

    public void enable() {
        this.status = StatusEnum.NORMAL;
    }

    public void disable() {
        this.status = StatusEnum.DISABLE;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getOrgType() {
        return orgType;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getAncestors() {
        return ancestors;
    }

    public Integer getLevel() {
        return level;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
