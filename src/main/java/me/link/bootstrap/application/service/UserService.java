package me.link.bootstrap.application.service;

import com.baomidou.mybatisplus.extension.service.IService;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import java.util.List;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.dto.request.user.UserCreateRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserPageRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.vo.UserResponseVO;
import me.link.bootstrap.shared.kernel.valueobject.PageResult;

public interface UserService extends IService<UserPO> {

    UserResponseVO create(UserCreateRequest request);
    UserResponseVO get(Long id);
    PageResult<UserResponseVO> page(UserPageRequest request);
    UserResponseVO update(Long id, UserUpdateRequest request);
    void delete(Long id);
    List<UserPO> findByUsernameForLogin(String username);
    List<UserPO> findByEmailForLogin(String email);
}
