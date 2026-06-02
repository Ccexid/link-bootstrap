package me.link.bootstrap.application.service;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateTenantPackageCommand;
import me.link.bootstrap.application.command.TenantPackagePageQuery;
import me.link.bootstrap.application.command.UpdateTenantPackageCommand;
import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.domain.factory.TenantPackageFactory;
import me.link.bootstrap.domain.repository.TenantPackageRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TenantPackageApplicationService {

    private final TenantPackageRepository tenantPackageRepository;

    @Transactional
    public TenantPackageEntity create(CreateTenantPackageCommand command) {
        TenantPackageEntity tenantPackage = TenantPackageFactory.create(command.name(), command.remark(), command.menuIds());
        return tenantPackageRepository.save(tenantPackage);
    }

    public TenantPackageEntity get(Long id) {
        return tenantPackageRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.TENANT_PACKAGE_NOT_FOUND));
    }

    public PageResult<TenantPackageEntity> page(TenantPackagePageQuery query) {
        return tenantPackageRepository.page(query.pageNo(), query.pageSize(), query.name(), query.sortingFields());
    }

    @Transactional
    public TenantPackageEntity update(UpdateTenantPackageCommand command) {
        TenantPackageEntity tenantPackage = get(command.id());
        TenantPackageFactory.changeBasicInfo(tenantPackage, command.name(), command.remark(), command.menuIds());
        if (Boolean.TRUE.equals(command.enabled())) {
            tenantPackage.enable();
        }
        if (Boolean.FALSE.equals(command.enabled())) {
            tenantPackage.disable();
        }
        boolean updated = tenantPackageRepository.update(tenantPackage);
        if (!updated) {
            throw new BusinessException(ErrorCode.TENANT_PACKAGE_NOT_FOUND);
        }
        return get(command.id());
    }

    @Transactional
    public void delete(Long id) {
        if (!tenantPackageRepository.deleteById(id)) {
            throw new BusinessException(ErrorCode.TENANT_PACKAGE_NOT_FOUND);
        }
    }
}
