package me.link.bootstrap.infrastructure.persistence.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.repository.UserRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.crypto.MobileCryptoService;
import me.link.bootstrap.infrastructure.crypto.ProtectedMobile;
import me.link.bootstrap.infrastructure.persistence.converter.UserConverter;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.infrastructure.persistence.repository.support.PageOrderHelper;
import me.link.bootstrap.shared.kernel.database.mybatis.TenantIgnore;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户仓储实现，负责将领域仓储抽象适配到 MyBatis-Plus 持久化能力。
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

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
    private final UserConverter userConverter;
    private final MobileCryptoService mobileCryptoService;

    @Override
    public UserEntity save(UserEntity user) {
        UserPO userPO = userConverter.convert(user);
        applyMobileProtection(userPO, user.getMobile());
        userInternalService.save(userPO);
        return userConverter.reverseConvert(userPO);
    }

    @Override
    public boolean update(UserEntity user) {
        UserPO userPO = userConverter.convert(user);
        applyMobileProtection(userPO, user.getMobile());
        return userInternalService.updateById(userPO);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return Optional.ofNullable(userInternalService.getById(id))
                .map(userConverter::reverseConvert);
    }

    @Override
    @TenantIgnore
    public List<UserEntity> findByUsername(String username) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUsername, username);
        return userConverter.reverseConvertList(userInternalService.list(wrapper));
    }

    @Override
    @TenantIgnore
    public List<UserEntity> findByMobile(String mobile) {
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getMobileHash, mobileCryptoService.hashForLookup(mobile));
        return userConverter.reverseConvertList(userInternalService.list(wrapper));
    }

    @Override
    public PageResult<UserEntity> page(Integer pageNo, Integer pageSize, String username, String nickname, String mobile, String email, Integer userType, StatusEnum status, Long tenantId, List<SortingField> sortingFields) {
        Page<UserPO> page = Page.of(pageNo, pageSize);
        PageOrderHelper.applyOrders(page, sortingFields, SORT_FIELD_MAPPING);
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .like(StrUtil.isNotBlank(username), UserPO::getUsername, username)
                .like(StrUtil.isNotBlank(nickname), UserPO::getNickname, nickname)
                .eq(StrUtil.isNotBlank(mobile), UserPO::getMobileHash, mobileCryptoService.hashForLookup(mobile))
                .like(StrUtil.isNotBlank(email), UserPO::getEmail, email)
                .eq(userType != null, UserPO::getUserType, userType)
                .eq(status != null, UserPO::getStatus, status)
                .eq(tenantId != null, UserPO::getTenantId, tenantId)
                .orderByDesc(sortingFields == null || sortingFields.isEmpty(), UserPO::getId);
        Page<UserPO> result = userInternalService.page(page, wrapper);
        return new PageResult<>(userConverter.reverseConvertList(result.getRecords()), result.getTotal());
    }

    @Override
    public boolean deleteById(Long id) {
        return userInternalService.removeById(id);
    }

    private void applyMobileProtection(UserPO userPO, String mobile) {
        ProtectedMobile protectedMobile = mobileCryptoService.protect(mobile);
        userPO.setMobileCipher(protectedMobile.cipher());
        userPO.setMobileHash(protectedMobile.hash());
        userPO.setMobileMask(protectedMobile.mask());
        userPO.setMobileKeyVersion(protectedMobile.keyVersion());
    }

}
