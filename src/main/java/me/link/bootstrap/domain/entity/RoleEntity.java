package me.link.bootstrap.domain.entity;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public class RoleEntity {

    private Long id;

    private String name;

    private String code;

    private Integer sort;

    private Integer dataScope;

    private String dataScopeDeptIds;

    private StatusEnum status;

    private Integer type;

    private String remark;

    private Long tenantId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private RoleEntity(Long id, String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sort = sort;
        this.dataScope = dataScope;
        this.dataScopeDeptIds = dataScopeDeptIds;
        this.status = status;
        this.type = type;
        this.remark = remark;
        this.tenantId = tenantId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static RoleEntity create(String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId) {
        return new RoleEntity(null, name, code, sort, dataScope, dataScopeDeptIds, status, type, remark, tenantId, null, null);
    }

    public static RoleEntity restore(Long id, String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new RoleEntity(id, name, code, sort, dataScope, dataScopeDeptIds, status, type, remark, tenantId, createdAt, updatedAt);
    }

    public void changeBasicInfo(String name, String code, Integer sort, Integer dataScope, String dataScopeDeptIds, StatusEnum status, Integer type, String remark, Long tenantId) {
        this.name = name;
        this.code = code;
        this.sort = sort;
        this.dataScope = dataScope;
        this.dataScopeDeptIds = dataScopeDeptIds;
        this.status = status;
        this.type = type;
        this.remark = remark;
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

    public String getCode() {
        return code;
    }

    public Integer getSort() {
        return sort;
    }

    public Integer getDataScope() {
        return dataScope;
    }

    public String getDataScopeDeptIds() {
        return dataScopeDeptIds;
    }

    public StatusEnum getStatus() {
        return status;
    }

    public Integer getType() {
        return type;
    }

    public String getRemark() {
        return remark;
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
