package me.link.bootstrap.domain.entity;

import java.time.LocalDateTime;

/**
 * 角色菜单关联领域实体，表示角色与菜单权限之间的授权关系。
 */
public class RoleMenuEntity {

    private Long id;

    private Long roleId;

    private Long menuId;

    private Long tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private RoleMenuEntity(Long id, Long roleId, Long menuId, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.roleId = roleId;
        this.menuId = menuId;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RoleMenuEntity create(Long roleId, Long menuId, Long tenantId) {
        return new RoleMenuEntity(null, roleId, menuId, tenantId, null, null);
    }

    public static RoleMenuEntity restore(Long id, Long roleId, Long menuId, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new RoleMenuEntity(id, roleId, menuId, tenantId, createdAt, updatedAt);
    }

    public void changeBasicInfo(Long roleId, Long menuId, Long tenantId) {
        this.roleId = roleId;
        this.menuId = menuId;
        this.tenantId = tenantId;
    }


    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getMenuId() {
        return menuId;
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
