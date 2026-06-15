package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.infrastructure.persistence.po.UserRolePO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用户角色关联持久化转换器，负责 UserRoleEntity 与 UserRolePO 之间的双向转换。
 */
@Mapper(config = BaseConverter.class)
public interface UserRoleConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    UserRolePO convert(UserRoleEntity userRoleEntity);

    default UserRoleEntity reverseConvert(UserRolePO userRolePO) {
        if (userRolePO == null) {
            return null;
        }
        return UserRoleEntity.restore(
                        userRolePO.getId(),
                        userRolePO.getUserId(),
                        userRolePO.getRoleId(),
                        userRolePO.getTenantId(),
                        userRolePO.getCreateTime(),
                        userRolePO.getUpdateTime()
        );
    }

    List<UserRolePO> convertList(List<UserRoleEntity> sourceList);

    List<UserRoleEntity> reverseConvertList(List<UserRolePO> targetList);
}
