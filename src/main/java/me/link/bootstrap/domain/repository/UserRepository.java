package me.link.bootstrap.domain.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    UserEntity save(UserEntity user);

    boolean update(UserEntity user);

    Optional<UserEntity> findById(Long id);

    PageResult<UserEntity> page(Integer pageNo, Integer pageSize, String username, String nickname, String mobile, Integer userType, StatusEnum status, Long tenantId, List<SortingField> sortingFields);

    boolean deleteById(Long id);
}
