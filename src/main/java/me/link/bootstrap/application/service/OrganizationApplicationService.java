package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateOrganizationCommand;
import me.link.bootstrap.application.command.OrganizationPageQuery;
import me.link.bootstrap.application.command.UpdateOrganizationCommand;
import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.domain.factory.OrganizationFactory;
import me.link.bootstrap.domain.repository.OrganizationRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 组织应用服务，负责编排组织创建、查询、更新和删除流程。
 * <p>
 * 租户ID从当前登录用户的上下文中自动获取，确保数据隔离安全性。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    private final OrganizationRepository organizationRepository;

    /**
     * 创建组织。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public OrganizationEntity create(CreateOrganizationCommand command) {
        Long tenantId = SecurityHelper.getTenantId();
        OrganizationEntity organization = OrganizationFactory.create(command.name(), command.orgType(), command.parentId(), command.ancestors(), command.level(), command.contactName(), command.contactMobile(), command.status(), tenantId);
        return organizationRepository.save(organization);
    }

    /**
     * 根据主键查询组织详情。
     */
    public OrganizationEntity get(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND));
    }

    /**
     * 分页查询组织列表。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    public PageResult<OrganizationEntity> page(OrganizationPageQuery query) {
        Long tenantId = SecurityHelper.getTenantId();
        return organizationRepository.page(query.pageNo(), query.pageSize(), query.name(), command.orgType(), command.parentId(), command.status(), tenantId, query.sortingFields());
    }

    /**
     * 更新组织信息。
     * <p>
     * 租户ID从当前登录用户的上下文中自动获取。
     * </p>
     */
    @Transactional
    public OrganizationEntity update(UpdateOrganizationCommand command) {
        OrganizationEntity organization = get(command.id());
        Long tenantId = SecurityHelper.getTenantId();
        OrganizationFactory.changeBasicInfo(organization, command.name(), command.orgType(), command.parentId(), command.ancestors(), command.level(), command.contactName(), command.contactMobile(), command.status(), tenantId);
        boolean updated = organizationRepository.update(organization);
        if (!updated) {
            throw new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND);
        }
        return get(command.id());
    }

    /**
     * 删除组织。
     */
    @Transactional
    public void delete(Long id) {
        if (!organizationRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND);
        }
    }
}
