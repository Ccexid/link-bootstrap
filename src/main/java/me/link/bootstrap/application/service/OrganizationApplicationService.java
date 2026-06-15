package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.application.command.CreateOrganizationCommand;
import me.link.bootstrap.application.command.OrganizationPageQuery;
import me.link.bootstrap.application.command.UpdateOrganizationCommand;
import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.factory.OrganizationFactory;
import me.link.bootstrap.domain.repository.OrganizationRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织应用服务，负责编排组织创建、查询、更新和删除流程。
 * <p>
 * 多租户隔离由 {@code TenantLineInnerInterceptor} 全局处理：所有针对组织表
 * 的 SELECT/UPDATE/DELETE 自动追加 {@code tenant_id = ?} 条件，规避水平越权（IDOR）。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    private final OrganizationRepository organizationRepository;

    /**
     * 创建组织。
     */
    @Transactional
    public OrganizationEntity create(CreateOrganizationCommand command) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        OrganizationEntity organization = OrganizationFactory.create(command.name(), command.orgType(), command.parentId(), command.ancestors(), command.level(), command.contactName(), command.contactMobile(), command.status(), tenantId);
        return organizationRepository.save(organization);
    }

    /**
     * 根据主键查询组织详情。
     */
    public OrganizationEntity get(Long id) {
        return ApplicationAssert.requireFound(organizationRepository.findById(id), ErrorCode.ORGANIZATION_NOT_FOUND);
    }

    /**
     * 分页查询组织列表。
     */
    public PageResult<OrganizationEntity> page(OrganizationPageQuery query) {
        return organizationRepository.page(query.pageNo(), query.pageSize(), query.name(), query.orgType(), query.parentId(), query.status(), null, query.sortingFields());
    }

    /**
     * 更新组织信息。
     */
    @Transactional
    public OrganizationEntity update(UpdateOrganizationCommand command) {
        OrganizationEntity organization = get(command.id());
        Long tenantId = SecurityHelper.getRequiredTenantId();
        OrganizationFactory.changeBasicInfo(organization, command.name(), command.orgType(), command.parentId(), command.ancestors(), command.level(), command.contactName(), command.contactMobile(), command.status(), tenantId);
        ApplicationAssert.requireSuccess(organizationRepository.update(organization), ErrorCode.ORGANIZATION_NOT_FOUND);
        return get(command.id());
    }

    /**
     * 删除组织。
     */
    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(organizationRepository.deleteById(id), ErrorCode.ORGANIZATION_NOT_FOUND);
    }
}
