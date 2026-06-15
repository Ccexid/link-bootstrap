package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.RoleEntity;
import me.link.bootstrap.infrastructure.persistence.po.RolePO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 角色持久化转换器，负责 RoleEntity 与 RolePO 之间的双向转换。
 */
@Mapper(config = BaseConverter.class)
public interface RoleConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    RolePO convert(RoleEntity roleEntity);

    default RoleEntity reverseConvert(RolePO rolePO) {
        if (rolePO == null) {
            return null;
        }
        return RoleEntity.restore(
                        rolePO.getId(),
                        rolePO.getName(),
                        rolePO.getCode(),
                        rolePO.getSort(),
                        rolePO.getDataScope(),
                        rolePO.getDataScopeDeptIds(),
                        rolePO.getStatus(),
                        rolePO.getType(),
                        rolePO.getRemark(),
                        rolePO.getTenantId(),
                        rolePO.getCreateTime(),
                        rolePO.getUpdateTime()
        );
    }

    List<RolePO> convertList(List<RoleEntity> sourceList);

    List<RoleEntity> reverseConvertList(List<RolePO> targetList);
}
