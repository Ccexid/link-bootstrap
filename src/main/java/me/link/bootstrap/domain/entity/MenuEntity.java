package me.link.bootstrap.domain.entity;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public class MenuEntity {

    private Long id;

    private String name;

    private String permission;

    private Integer type;

    private Integer sort;

    private Long parentId;

    private String path;

    private String icon;

    private String component;

    private String componentName;

    private StatusEnum status;

    private Boolean visible;

    private Boolean keepAlive;

    private Boolean alwaysShow;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private MenuEntity(Long id, String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.permission = permission;
        this.type = type;
        this.sort = sort;
        this.parentId = parentId;
        this.path = path;
        this.icon = icon;
        this.component = component;
        this.componentName = componentName;
        this.status = status;
        this.visible = visible;
        this.keepAlive = keepAlive;
        this.alwaysShow = alwaysShow;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MenuEntity create(String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        return new MenuEntity(null, name, permission, type, sort, parentId, path, icon, component, componentName, status, visible, keepAlive, alwaysShow, null, null);
    }

    public static MenuEntity restore(Long id, String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new MenuEntity(id, name, permission, type, sort, parentId, path, icon, component, componentName, status, visible, keepAlive, alwaysShow, createdAt, updatedAt);
    }

    public void changeBasicInfo(String name, String permission, Integer type, Integer sort, Long parentId, String path, String icon, String component, String componentName, StatusEnum status, Boolean visible, Boolean keepAlive, Boolean alwaysShow) {
        this.name = name;
        this.permission = permission;
        this.type = type;
        this.sort = sort;
        this.parentId = parentId;
        this.path = path;
        this.icon = icon;
        this.component = component;
        this.componentName = componentName;
        this.status = status;
        this.visible = visible;
        this.keepAlive = keepAlive;
        this.alwaysShow = alwaysShow;
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

    public String getPermission() {
        return permission;
    }

    public Integer getType() {
        return type;
    }

    public Integer getSort() {
        return sort;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getPath() {
        return path;
    }

    public String getIcon() {
        return icon;
    }

    public String getComponent() {
        return component;
    }

    public String getComponentName() {
        return componentName;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public Boolean getVisible() {
        return visible;
    }

    public Boolean getKeepAlive() {
        return keepAlive;
    }

    public Boolean getAlwaysShow() {
        return alwaysShow;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
