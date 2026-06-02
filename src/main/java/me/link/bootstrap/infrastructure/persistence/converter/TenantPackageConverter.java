package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.TenantPackageEntity;
import me.link.bootstrap.infrastructure.persistence.po.TenantPackagePO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 租户套餐持久化转换器，负责 TenantPackageEntity 与 TenantPackagePO 之间的双向转换。
 */
@Mapper(config = BaseConverter.class)
public interface TenantPackageConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    TenantPackagePO convert(TenantPackageEntity tenantPackageEntity);

    default TenantPackageEntity reverseConvert(TenantPackagePO tenantPackagePO) {
        if (tenantPackagePO == null) {
            return null;
        }
        return TenantPackageEntity.restore(
                tenantPackagePO.getId(),
                tenantPackagePO.getName(),
                tenantPackagePO.getStatus(),
                tenantPackagePO.getRemark(),
                tenantPackagePO.getMenuIds(),
                tenantPackagePO.getCreateTime(),
                tenantPackagePO.getUpdateTime()
        );
    }

    List<TenantPackagePO> convertList(List<TenantPackageEntity> sourceList);

    List<TenantPackageEntity> reverseConvertList(List<TenantPackagePO> targetList);
}
