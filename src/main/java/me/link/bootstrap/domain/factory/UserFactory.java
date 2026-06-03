package me.link.bootstrap.domain.factory;

import cn.hutool.crypto.digest.BCrypt;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;

public final class UserFactory {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 64;

    private UserFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static UserEntity create(String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        validate(username, password, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return UserEntity.create(username, encryptedPassword, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
    }

    public static void changeBasicInfo(UserEntity user, String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        validate(username, password, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.changeBasicInfo(username, encryptedPassword, nickname, userType, mobile, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
    }

    private static void validate(String username, String password, String nickname, Integer userType, String mobile, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("用户username不能为空");
        }
        if (nickname == null || nickname.trim().isEmpty()) {
            throw new IllegalArgumentException("用户nickname不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("用户密码不能为空");
        }
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException("用户密码长度必须在 " + PASSWORD_MIN_LENGTH + " 到 " + PASSWORD_MAX_LENGTH + " 之间");
        }
    }
}
