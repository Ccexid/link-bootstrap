package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateTenantCommand;
import me.link.bootstrap.application.command.TenantPageQuery;
import me.link.bootstrap.application.command.UpdateTenantCommand;
import me.link.bootstrap.domain.entity.TenantEntity;
import me.link.bootstrap.domain.factory.TenantFactory;
import me.link.bootstrap.domain.repository.TenantRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantApplicationService {

    private final TenantRepository tenantRepository;

    @Transactional
    public TenantEntity create(CreateTenantCommand command) {
        TenantEntity tenant = TenantFactory.create(
                command.name(),
                command.contactUserId(),
                command.contactName(),
                command.contactMobile(),
                command.websites(),
                command.packageId(),
                command.expireTime(),
                command.accountCount()
        );
        return tenantRepository.save(tenant);
    }

    public TenantEntity get(Long id) {
        return tenantRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TENANT_NOT_FOUND));
    }

    public PageResult<TenantEntity> page(TenantPageQuery query) {
        return tenantRepository.page(query.pageNo(), query.pageSize(), query.name(), query.sortingFields());
    }

    @Transactional
    public TenantEntity update(UpdateTenantCommand command) {
        TenantEntity tenant = get(command.id());
        TenantFactory.changeContact(tenant, command.contactUserId(), command.contactName(), command.contactMobile());
        TenantFactory.changeWebsites(tenant, command.websites());
        TenantFactory.changePackage(tenant, command.packageId(), command.expireTime(), command.accountCount());
        if (Boolean.TRUE.equals(command.enabled())) {
            tenant.enable();
        }
        if (Boolean.FALSE.equals(command.enabled())) {
            tenant.disable();
        }
        boolean updated = tenantRepository.update(tenant);
        if (!updated) {
            throw new BusinessException(ErrorCode.TENANT_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!tenantRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.TENANT_NOT_FOUND);
        }
    }
}
