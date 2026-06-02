package me.link.bootstrap.domain.factory;

import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public final class UserFactory {

    private UserFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static UserEntity create(String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        validate(username, password, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
        return UserEntity.create(username, password, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
    }

    public static void changeBasicInfo(UserEntity user, String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        validate(username, password, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
        user.changeBasicInfo(username, password, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
    }

    private static void validate(String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户username不能为空");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("用户nickname不能为空");
        }
    }
}
