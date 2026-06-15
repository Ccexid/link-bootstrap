package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.MenuEntity;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 菜单持久化转换器，负责 MenuEntity 与 MenuPO 之间的双向转换。
 */
@Mapper(config = BaseConverter.class)
public interface MenuConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    MenuPO convert(MenuEntity menuEntity);

    default MenuEntity reverseConvert(MenuPO menuPO) {
        if (menuPO == null) {
            return null;
        }
        return MenuEntity.restore(
                        menuPO.getId(),
                        menuPO.getName(),
                        menuPO.getPermission(),
                        menuPO.getType(),
                        menuPO.getSort(),
                        menuPO.getParentId(),
                        menuPO.getPath(),
                        menuPO.getIcon(),
                        menuPO.getComponent(),
                        menuPO.getComponentName(),
                        menuPO.getStatus(),
                        menuPO.getVisible(),
                        menuPO.getKeepAlive(),
                        menuPO.getAlwaysShow(),
                        menuPO.getCreateTime(),
                        menuPO.getUpdateTime()
        );
    }

    List<MenuPO> convertList(List<MenuEntity> sourceList);

    List<MenuEntity> reverseConvertList(List<MenuPO> targetList);
}
