package me.link.bootstrap.application.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.support.ApplicationAssert;
import me.link.bootstrap.shared.kernel.valueobject.StatusEnum;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoService;
import me.link.bootstrap.infrastructure.crypto.ProtectedMobile;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.persistence.support.PageOrderHelper;
import me.link.bootstrap.interfaces.dto.request.user.UserCreateRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserPageRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserUpdateRequest;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantIgnore;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户应用服务，负责编排用户创建、查询、更新和删除流程。
 * <p>
 * 用户模块采用轻量三层结构，直接使用 UserPO 和 MyBatis-Plus InternalService；
 * 登录前账号解析所需的跨租户查询仅在对应方法上使用 {@link TenantIgnore}。
 * </p>
 */
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private static final int PASSWORD_MIN_LENGTH = 8;
    private static final int PASSWORD_MAX_LENGTH = 64;
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "username", "username",
            "mobile", "mobile_mask",
            "email", "email",
            "tenant_id", "tenant_id"
    );

    private final UserInternalService userInternalService;
    private final MobileCryptoService mobileCryptoService;

    /**
     * 创建用户。
     */
    @Transactional
    public UserPO create(UserCreateRequest request) {
        Long tenantId = SecurityHelper.getRequiredTenantId();
        UserPO user = new UserPO();
        fillUser(user, request.getUsername(), request.getPassword(), request.getNickname(), request.getUserType(), request.getMobile(), request.getEmail(), request.getAvatar(), request.getStatus(), request.getOrgId(), request.getDeptId(), request.getLoginIp(), request.getLoginDate(), tenantId);
        userInternalService.save(user);
        return user;
    }

    /**
     * 根据主键查询用户详情。
     */
    public UserPO get(Long id) {
        return ApplicationAssert.requireFound(userInternalService.getById(id), ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 分页查询用户列表。
     */
    public PageResult<UserPO> page(UserPageRequest request) {
        Page<UserPO> page = Page.of(request.getPageNo(), request.getPageSize());
        PageOrderHelper.applyOrders(page, request.getSortingFields(), SORT_FIELD_MAPPING);
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .like(StrUtil.isNotBlank(request.getUsername()), UserPO::getUsername, request.getUsername())
                .like(StrUtil.isNotBlank(request.getNickname()), UserPO::getNickname, request.getNickname())
                .eq(StrUtil.isNotBlank(request.getMobile()), UserPO::getMobileHash, mobileCryptoService.hashForLookup(request.getMobile()))
                .like(StrUtil.isNotBlank(request.getEmail()), UserPO::getEmail, request.getEmail())
                .eq(request.getUserType() != null, UserPO::getUserType, request.getUserType())
                .eq(request.getStatus() != null, UserPO::getStatus, request.getStatus())
                .orderByDesc(request.getSortingFields() == null || request.getSortingFields().isEmpty(), UserPO::getId);
        Page<UserPO> result = userInternalService.page(page, wrapper);
        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    /**
     * 更新用户信息。
     */
    @Transactional
    public UserPO update(Long id, UserUpdateRequest request) {
        UserPO user = get(id);
        Long tenantId = SecurityHelper.getRequiredTenantId();
        fillUser(user, request.getUsername(), request.getPassword(), request.getNickname(), request.getUserType(), request.getMobile(), request.getEmail(), request.getAvatar(), request.getStatus(), request.getOrgId(), request.getDeptId(), request.getLoginIp(), request.getLoginDate(), tenantId);
        ApplicationAssert.requireSuccess(userInternalService.updateById(user), ErrorCode.USER_NOT_FOUND);
        return get(id);
    }

    /**
     * 删除用户。
     */
    @Transactional
    public void delete(Long id) {
        ApplicationAssert.requireSuccess(userInternalService.removeById(id), ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 登录前按用户名跨租户查询。此时尚无 Sa-Token 会话，只能在最小方法范围绕过租户拦截。
     */
    @TenantIgnore
    public List<UserPO> findByUsernameForLogin(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUsername, username);
        return userInternalService.list(wrapper);
    }

    /**
     * 登录前按邮箱跨租户查询。后续认证成功后再把用户 tenantId 写入 Sa-Token Session。
     */
    @TenantIgnore
    public List<UserPO> findByEmailForLogin(String email) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getEmail, StrUtil.trim(email));
        return userInternalService.list(wrapper);
    }

    private void fillUser(UserPO user, String username, String password, String nickname, Integer userType, String mobile,
                          String email, String avatar, StatusEnum status, Long orgId, Long deptId, String loginIp,
                          LocalDateTime loginDate, Long tenantId) {
        validate(username, password, nickname, mobile, email);
        ProtectedMobile protectedMobile = mobileCryptoService.protect(mobile);

        user.setUsername(username.trim());
        user.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
        user.setNickname(nickname.trim());
        user.setUserType(userType);
        user.setMobileCipher(protectedMobile.cipher());
        user.setMobileHash(protectedMobile.hash());
        user.setMobileMask(protectedMobile.mask());
        user.setMobileKeyVersion(protectedMobile.keyVersion());
        user.setEmail(normalizeEmail(email));
        user.setAvatar(avatar);
        user.setStatus(status);
        user.setOrgId(orgId);
        user.setDeptId(deptId);
        user.setLoginIp(loginIp);
        user.setLoginDate(loginDate);
        user.setTenantId(tenantId);
    }

    private void validate(String username, String password, String nickname, String mobile, String email) {
        if (StrUtil.isBlank(username)) {
            throw new IllegalArgumentException("用户username不能为空");
        }
        if (StrUtil.isBlank(nickname)) {
            throw new IllegalArgumentException("用户nickname不能为空");
        }
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("用户密码不能为空");
        }
        if (password.length() < PASSWORD_MIN_LENGTH || password.length() > PASSWORD_MAX_LENGTH) {
            throw new IllegalArgumentException("用户密码长度必须在 " + PASSWORD_MIN_LENGTH + " 到 " + PASSWORD_MAX_LENGTH + " 之间");
        }
        if (StrUtil.isBlank(mobile)) {
            throw new IllegalArgumentException("用户手机号不能为空");
        }
        if (!MOBILE_PATTERN.matcher(mobile.trim()).matches()) {
            throw new IllegalArgumentException("用户手机号格式不正确");
        }
        if (StrUtil.isNotBlank(email) && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("用户邮箱格式不正确");
        }
    }

    private String normalizeEmail(String email) {
        return StrUtil.isBlank(email) ? null : email.trim();
    }
}
