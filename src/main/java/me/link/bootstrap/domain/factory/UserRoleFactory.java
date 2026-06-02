package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.UserRoleEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public final class UserRoleFactory {

    private UserRoleFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static UserRoleEntity create(Long userId, Long roleId, Long tenantId) {
        validate(userId, roleId, tenantId);
        return UserRoleEntity.create(userId, roleId, tenantId);
    }

    public static void changeBasicInfo(UserRoleEntity userRole, Long userId, Long roleId, Long tenantId) {
        if (userRole == null) {
            throw new IllegalArgumentException("用户角色关联不能为空");
        }
        validate(userId, roleId, tenantId);
        userRole.changeBasicInfo(userId, roleId, tenantId);
    }

    private static void validate(Long userId, Long roleId, Long tenantId) {
        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("用户角色关联userId必须大于0");
        }
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("用户角色关联roleId必须大于0");
        }
    }
}
