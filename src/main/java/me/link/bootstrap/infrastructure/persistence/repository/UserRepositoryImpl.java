package me.link.bootstrap.infrastructure.persistence.repository;

import me.link.bootstrap.domain.valueobject.StatusEnum;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.repository.UserRepository;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.converter.UserConverter;
import me.link.bootstrap.infrastructure.persistence.internal.UserInternalService;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.shared.kernel.valueobject.SortingField;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private static final Map<String, String> SORT_FIELD_MAPPING = Map.of(
            "id", "id",
            "created_at", "create_time",
            "updated_at", "update_time",
            "username", "username",
            "mobile", "mobile",
            "tenant_id", "tenant_id"
    );

    private final UserInternalService userInternalService;
    private final UserConverter userConverter;

    @Override
    public UserEntity save(UserEntity user) {
        UserPO userPO = userConverter.convert(user);
        userInternalService.save(userPO);
        return userConverter.reverseConvert(userPO);
    }

    @Override
    public boolean update(UserEntity user) {
        UserPO userPO = userConverter.convert(user);
        return userInternalService.updateById(userPO);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return Optional.ofNullable(userInternalService.getById(id))
                .map(userConverter::reverseConvert);
    }

    @Override
    public PageResult<UserEntity> page(Integer pageNo, Integer pageSize, String username, String nickname, String mobile, Integer userType, StatusEnum status, Long tenantId, List<SortingField> sortingFields) {
        Page<UserPO> page = Page.of(pageNo, pageSize);
        applyOrders(page, sortingFields);
        LambdaQueryWrapper<UserPO> wrapper = new LambdaQueryWrapper<UserPO>()
                .like(StrUtil.isNotBlank(username), UserPO::getUsername, username)
                .like(StrUtil.isNotBlank(nickname), UserPO::getNickname, nickname)
                .like(StrUtil.isNotBlank(mobile), UserPO::getMobile, mobile)
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

    private void applyOrders(Page<UserPO> page, List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return;
        }
        sortingFields.stream()
                .map(this::toOrderItem)
                .forEach(page::addOrder);
    }

    private OrderItem toOrderItem(SortingField sortingField) {
        String column = SORT_FIELD_MAPPING.get(sortingField.getField());
        if (sortingField.isAsc()) {
            return OrderItem.asc(column);
        }
        return OrderItem.desc(column);
    }
}
