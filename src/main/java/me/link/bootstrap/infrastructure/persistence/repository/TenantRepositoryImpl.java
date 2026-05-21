package me.link.bootstrap.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.repository.TenantRepository;
import me.link.bootstrap.infrastructure.persistence.converter.TenantConverter;
import me.link.bootstrap.infrastructure.persistence.internal.TenantInternalService;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TenantRepositoryImpl implements TenantRepository {
    // 引入内部服务
    private final TenantInternalService tenantInternalService;
    private final TenantConverter tenantConverter;
}
