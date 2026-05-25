package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.TenantEntity;
import me.link.bootstrap.infrastructure.persistence.po.TenantPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 租户对象转换器（Entity <-> PO）
 * <p>
 * 绑定全局统一的 BaseConverter 配置。
 * S (Source) = LinkEntity (领域实体)
 * T (Target) = LinkPO (数据库持久化对象)
 * </p>
 *
 * @author ccexid
 */
@Mapper(config = BaseConverter.class)
public interface TenantConverter extends BaseConverter<TenantEntity, TenantPO> {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    TenantPO convert(TenantEntity tenantPO);
}
