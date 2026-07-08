package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import java.util.List;
import me.link.bootstrap.interfaces.dto.request.user.UserCreateRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserPageRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.UserResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface UserService extends IService<UserPO> {

    /**
     * 创建用户。
     */
    UserResponseVO create(UserCreateRequest request);
    /**
     * 查询用户详情。
     */
    UserResponseVO get(Long id);
    /**
     * 分页查询用户列表。
     */
    PageResult<UserResponseVO> page(UserPageRequest request);
    /**
     * 更新用户。
     */
    UserResponseVO update(Long id, UserUpdateRequest request);
    /**
     * 删除用户。
     */
    void delete(Long id);
    /**
     * 登录前按用户名跨租户查询用户。
     */
    List<UserPO> findByUsernameForLogin(String username);
    /**
     * 登录前按邮箱跨租户查询用户。
     */
    List<UserPO> findByEmailForLogin(String email);
}
