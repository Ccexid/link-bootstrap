package me.link.bootstrap.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import me.link.bootstrap.infrastructure.persistence.po.MenuPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 权限聚合查询 Mapper。
 * <p>
 * 权限码来源于 {@code system_menu.permission},聚合 SQL 写在 {@code resources/mapper/PermissionMapper.xml}。
 * </p>
 * <p>
 * 调用时上下文必须已登录:{@code TenantLineInnerInterceptor} 会从 Sa-Token Session 取 tenantId,
 * 自动给 {@code system_user_role} / {@code system_role_menu} / {@code system_role} 拼接
 * {@code tenant_id = ?} 条件。{@code system_menu} 是全局表,不参与租户隔离。
 * </p>
 */
@Mapper
public interface PermissionMapper extends BaseMapper<MenuPO> {

    /**
     * 查询用户在当前租户下拥有的权限码列表(已去重,过滤空字符串,仅返回启用菜单)。
     */
    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户在当前租户下拥有的角色编码列表(已去重,仅返回启用角色)。
     */
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * 查询持有指定角色的所有用户 ID。
     * <p>用于权限缓存的级联失效:role / role_menu 变更后,需要清空所有受影响用户的缓存。</p>
     */
    List<Long> selectUserIdsByRoleId(@Param("roleId") Long roleId);
}
