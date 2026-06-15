package me.link.bootstrap.infrastructure.persistence.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.repository.OrganizationRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.OrganizationConverter;
import me.link.bootstrap.infrastructure.persistence.internal.OrganizationInternalService;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class OrganizationRepositoryImpl implements OrganizationRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "name", "name",
            "parent_id", "parent_id",
            "tenant_id", "tenant_id"
    );

    private final OrganizationInternalService organizationInternalService;
    private final OrganizationConverter organizationConverter;

    @Override
    public OrganizationEntity save(OrganizationEntity organization) {
        OrganizationPO organizationPO = organizationConverter.convert(organization);
        organizationInternalService.save(organizationPO);
        return organizationConverter.reverseConvert(organizationPO);
    }

    @Override
    public boolean update(OrganizationEntity organization) {
        OrganizationPO organizationPO = organizationConverter.convert(organization);
        return organizationInternalService.updateById(organizationPO);
    }

    @Override
    public Optional<OrganizationEntity> findById(Long id) {
        return Optional.ofNullable(organizationInternalService.getById(id))
                .map(organizationConverter::reverseConvert);
    }

    @Override
    public PageResult<OrganizationEntity> page(Integer pageNo, Integer pageSize, String name, Integer orgType, Long parentId, StatusEnum status, Long tenantId, List<SortingField> sortingFields) {
        Page<OrganizationPO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<OrganizationPO> wrapper = new LambdaQueryWrapper<OrganizationPO>()
                .like(StrUtil.isNotBlank(name), OrganizationPO::getName, name)
                .eq(orgType != null, OrganizationPO::getOrgType, orgType)
                .eq(parentId != null, OrganizationPO::getParentId, parentId)
                .eq(status != null, OrganizationPO::getStatus, status)
                .eq(tenantId != null, OrganizationPO::getTenantId, tenantId)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), OrganizationPO::getId);
        Page<OrganizationPO> result = organizationInternalService.page(page, wrapper);
        return new PageResult<>(organizationConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return organizationInternalService.removeById(id);
    }

}
