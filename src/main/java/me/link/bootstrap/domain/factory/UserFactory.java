package me.link.bootstrap.domain.factory;

import cn.hutool.crypto.digest.BCrypt;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.valueobject.StatusEnum;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

/**
 * 用户领域工厂，集中封装用户创建和变更校验，并负责密码加密。
 * <p>
 * 当前更新模型仍要求传入明文密码，因此 {@link #changeBasicInfo(UserEntity, String, String, String, Integer, String, String, String, StatusEnum, Long, Long, String, LocalDateTime, Long)}
 * 会重新生成密码摘要。若后续支持“不改密码的资料更新”，建议拆分为资料变更和密码变更两个领域方法。
 * </p>
 */
public final class UserFactory {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 64;
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private UserFactory() {
        throw new UnsupportedOperationException("工厂类不允许实例化");
    }

    public static UserEntity create(String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        validate(username, password, nickname, userType, mobile, email, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return UserEntity.create(username.trim(), encryptedPassword, nickname.trim(), userType, mobile, normalizeEmail(email), avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
    }

    public static void changeBasicInfo(UserEntity user, String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
        if (user == null) {
            throw new IllegalArgumentException("用户不能为空");
        }
        validate(username, password, nickname, userType, mobile, email, avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
        String encryptedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        user.changeBasicInfo(username.trim(), encryptedPassword, nickname.trim(), userType, mobile, normalizeEmail(email), avatar, status, orgId, deptId, loginIp, loginDate, tenantId);
    }

    private static void validate(String username, String password, String nickname, Integer userType, String mobile, String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp, LocalDateTime loginDate, Long tenantId) {
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
        if (mobile == null || mobile.trim().isEmpty()) {
            throw new IllegalArgumentException("用户手机号不能为空");
        }
        if (!MOBILE_PATTERN.matcher(mobile.trim()).matches()) {
            throw new IllegalArgumentException("用户手机号格式不正确");
        }
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("用户邮箱格式不正确");
        }
    }

    private static String normalizeEmail(String email) {
        return email == null || email.trim().isEmpty() ? null : email.trim();
    }
}
