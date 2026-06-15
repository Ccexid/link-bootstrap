package me.link.bootstrap.domain.entity;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

/**
 * 用户角色关联领域实体，表示用户与角色之间的授权关系。
 */
public class UserRoleEntity {

    private Long id;

    private Long userId;

    private Long roleId;

    private Long tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private UserRoleEntity(Long id, Long userId, Long roleId, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.roleId = roleId;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static UserRoleEntity create(Long userId, Long roleId, Long tenantId) {
        return new UserRoleEntity(null, userId, roleId, tenantId, null, null);
    }

    public static UserRoleEntity restore(Long id, Long userId, Long roleId, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new UserRoleEntity(id, userId, roleId, tenantId, createdAt, updatedAt);
    }

    public void changeBasicInfo(Long userId, Long roleId, Long tenantId) {
        this.userId = userId;
        this.roleId = roleId;
        this.tenantId = tenantId;
    }


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getRoleId() {
        return roleId;
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
