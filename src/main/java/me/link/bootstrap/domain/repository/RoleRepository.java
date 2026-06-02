package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface RoleRepository {

    RoleEntity save(RoleEntity role);

    boolean update(RoleEntity role);

    Optional<RoleEntity> findById(Long id);

    PageResult<RoleEntity> page(Integer pageNo, Integer pageSize, String name, String code, StatusEnum status, Integer type, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);

    /**
     * 根据租户ID和权限编码查询角色。
     * <p>
     * 用于校验角色编码在同一租户下的唯一性，结合Sa-Token上下文实现数据隔离。
     * </p>
     *
     * @param tenantId 租户ID（从当前登录用户上下文获取）
     * @param code     角色权限编码
     * @return 角色实体对象（Optional包装，可能为空）
     */
    Optional<RoleEntity> findByTenantIdAndCode(Long tenantId, String code);
}
