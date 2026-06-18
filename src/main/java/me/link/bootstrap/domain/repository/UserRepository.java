package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

/**
 * 用户仓储接口，定义领域层访问用户持久化数据所需的抽象能力。
 */
public interface UserRepository {

    UserEntity save(UserEntity user);

    boolean update(UserEntity user);

    Optional<UserEntity> findById(Long id);

    /**
     * 按用户名跨租户查询。
     * <p>登录场景使用:登录时尚无 Sa-Token 会话,LinkTenantLineHandler 无法自动注入 tenant_id,
     * 调用方需用 @TenantIgnore 绕过租户拦截器,由应用层处理多租户重名。</p>
     */
    List<UserEntity> findByUsername(String username);

    /**
     * 按手机号跨租户查询。
     * <p>登录场景使用:登录时尚无 Sa-Token 会话,LinkTenantLineHandler 无法自动注入 tenant_id,
     * 调用方需用 @TenantIgnore 绕过租户拦截器,由应用层处理多租户手机号重复。</p>
     */
    List<UserEntity> findByMobile(String mobile);

    PageResult<UserEntity> page(Integer pageNo, Integer pageSize, String username, String nickname, String mobile, String email, Integer userType, StatusEnum status, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
