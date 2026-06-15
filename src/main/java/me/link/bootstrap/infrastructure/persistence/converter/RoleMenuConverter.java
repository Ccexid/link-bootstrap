package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.RoleMenuEntity;
import me.link.bootstrap.infrastructure.persistence.po.RoleMenuPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 角色菜单关联持久化转换器，负责 RoleMenuEntity 与 RoleMenuPO 之间的双向转换。
 */
@Mapper(config = BaseConverter.class)
public interface RoleMenuConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    RoleMenuPO convert(RoleMenuEntity roleMenuEntity);

    default RoleMenuEntity reverseConvert(RoleMenuPO roleMenuPO) {
        if (roleMenuPO == null) {
            return null;
        }
        return RoleMenuEntity.restore(
                        roleMenuPO.getId(),
                        roleMenuPO.getRoleId(),
                        roleMenuPO.getMenuId(),
                        roleMenuPO.getTenantId(),
                        roleMenuPO.getCreateTime(),
                        roleMenuPO.getUpdateTime()
        );
    }

    List<RoleMenuPO> convertList(List<RoleMenuEntity> sourceList);

    List<RoleMenuEntity> reverseConvertList(List<RoleMenuPO> targetList);
}
