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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrganizationApplicationService {

    private final OrganizationRepository organizationRepository;

    @Transactional
    public OrganizationEntity create(CreateOrganizationCommand command) {
        OrganizationEntity organization = OrganizationFactory.create(command.name(), command.orgType(), command.parentId(), command.ancestors(), command.level(), command.contactName(), command.contactMobile(), command.status(), command.tenantId());
        return organizationRepository.save(organization);
    }

    public OrganizationEntity get(Long id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND));
    }

    public PageResult<OrganizationEntity> page(OrganizationPageQuery query) {
        return organizationRepository.page(query.pageNo(), query.pageSize(), query.name(), query.orgType(), query.parentId(), query.status(), query.tenantId(), query.sortingFields());
    }

    @Transactional
    public OrganizationEntity update(UpdateOrganizationCommand command) {
        OrganizationEntity organization = get(command.id());
        OrganizationFactory.changeBasicInfo(organization, command.name(), command.orgType(), command.parentId(), command.ancestors(), command.level(), command.contactName(), command.contactMobile(), command.status(), command.tenantId());
        boolean updated = organizationRepository.update(organization);
        if (!updated) {
            throw new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!organizationRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.ORGANIZATION_NOT_FOUND);
        }
    }
}
