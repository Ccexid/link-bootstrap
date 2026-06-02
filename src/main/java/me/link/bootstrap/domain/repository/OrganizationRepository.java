package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {

    OrganizationEntity save(OrganizationEntity organization);

    boolean update(OrganizationEntity organization);

    Optional<OrganizationEntity> findById(Long id);

    PageResult<OrganizationEntity> page(Integer pageNo, Integer pageSize, String name, Integer orgType, Long parentId, StatusEnum status, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
