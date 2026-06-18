package me.link.bootstrap.infrastructure.persistence.converter;

import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.shared.kernel.converter.BaseConverter;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * 用户持久化转换器，负责 UserEntity 与 UserPO 之间的双向转换。
 */
@Mapper(config = BaseConverter.class)
public interface UserConverter extends BaseConverter {

    @Mapping(target = "updater", ignore = true)
    @Mapping(target = "updateTime", ignore = true)
    @Mapping(target = "creator", ignore = true)
    @Mapping(target = "createTime", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "mobileCipher", ignore = true)
    @Mapping(target = "mobileHash", ignore = true)
    @Mapping(target = "mobileMask", ignore = true)
    @Mapping(target = "mobileKeyVersion", ignore = true)
    UserPO convert(UserEntity userEntity);

    default UserEntity reverseConvert(UserPO userPO) {
        if (userPO == null) {
            return null;
        }
        return UserEntity.restore(
                        userPO.getId(),
                        userPO.getUsername(),
                        userPO.getPassword(),
                        userPO.getNickname(),
                        userPO.getUserType(),
                        userPO.getMobileMask(),
                        userPO.getEmail(),
                        userPO.getAvatar(),
                        userPO.getStatus(),
                        userPO.getOrgId(),
                        userPO.getDeptId(),
                        userPO.getLoginIp(),
                        userPO.getLoginDate(),
                        userPO.getTenantId(),
                        userPO.getCreateTime(),
                        userPO.getUpdateTime()
        );
    }

    List<UserPO> convertList(List<UserEntity> sourceList);

    List<UserEntity> reverseConvertList(List<UserPO> targetList);
}
