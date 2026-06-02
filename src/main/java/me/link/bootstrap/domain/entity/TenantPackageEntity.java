package me.link.bootstrap.domain.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@ToString
@EqualsAndHashCode
public class TenantPackageEntity {

    private final Long id;

    private String name;

    private StatusEnum status;

    private String remark;

    private Set<Long> menuIds;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    private TenantPackageEntity(Long id,
                                String name,
                                StatusEnum status,
                                String remark,
                                Set<Long> menuIds,
                                LocalDateTime createdAt,
                                LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.remark = remark;
        this.menuIds = copyMenuIds(menuIds);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static TenantPackageEntity create(String name, String remark, Set<Long> menuIds) {
        return new TenantPackageEntity(null, name, StatusEnum.NORMAL, remark, menuIds, null, null);
    }

    public static TenantPackageEntity restore(Long id,
                                              String name,
                                              StatusEnum status,
                                              String remark,
                                              Set<Long> menuIds,
                                              LocalDateTime createdAt,
                                              LocalDateTime updatedAt) {
        return new TenantPackageEntity(id, name, status, remark, menuIds, createdAt, updatedAt);
    }

    public Set<Long> getMenuIds() {
        if (menuIds == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableSet(menuIds);
    }

    public void changeBasicInfo(String name, String remark, Set<Long> menuIds) {
        this.name = name;
        this.remark = remark;
        this.menuIds = copyMenuIds(menuIds);
    }

    public void disable() {
        this.status = StatusEnum.DISABLE;
    }

    public void enable() {
        this.status = StatusEnum.NORMAL;
    }

    private static Set<Long> copyMenuIds(Set<Long> menuIds) {
        if (menuIds == null) {
            return null;
        }
        return new LinkedHashSet<>(menuIds);
    }
}
