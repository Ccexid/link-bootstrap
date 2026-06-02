package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.OrganizationEntity;
import me.link.bootstrap.infrastructure.persistence.po.OrganizationPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(config = BaseConverter.class)
public interface OrganizationConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    OrganizationPO convert(OrganizationEntity organizationEntity);

    default OrganizationEntity reverseConvert(OrganizationPO organizationPO) {
        if (organizationPO == null) {
            return null;
        }
        return OrganizationEntity.restore(
                        organizationPO.getId(),
                        organizationPO.getName(),
                        organizationPO.getOrgType(),
                        organizationPO.getParentId(),
                        organizationPO.getAncestors(),
                        organizationPO.getLevel(),
                        organizationPO.getContactName(),
                        organizationPO.getContactMobile(),
                        organizationPO.getStatus(),
                        organizationPO.getTenantId(),
                        organizationPO.getCreateTime(),
                        organizationPO.getUpdateTime()
        );
    }

    List<OrganizationPO> convertList(List<OrganizationEntity> sourceList);

    List<OrganizationEntity> reverseConvertList(List<OrganizationPO> targetList);
}
