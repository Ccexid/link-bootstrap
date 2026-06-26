package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.UserApplicationService;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.user.UserCreateRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserPageRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.UserResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
import me.link.bootstrap.shared.kernel.annotation.Idempotent;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户接口控制器，对外提供用户增删改查 REST 接口。
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "用户接口", description = "用户增删改查接口")
public class UserController {

    private final UserApplicationService userApplicationService;
    private final ResponseVOConverter responseVOConverter;

    @PostMapping
    @SaCheckPermission("system:user:create")
    @Idempotent
    @Operation(summary = "创建用户", description = "创建用户基础信息")
    public ResultResponse<UserResponseVO> create(@Valid @RequestBody UserCreateRequest request) {
        UserPO user = userApplicationService.create(request);
        return ResultResponse.success(responseVOConverter.toResponse(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据ID查询用户详情")
    public ResultResponse<UserResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(responseVOConverter.toResponse(userApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    public ResultTableResponse<UserResponseVO> page(@Validated @SortWhitelist(UserResponseVO.class) UserPageRequest request) {
        PageResult<UserPO> pageResult = userApplicationService.page(request);
        return ResultTableResponse.success(pageResult, responseVOConverter::toResponse);
    }

    @PutMapping("/{id}")
    @SaCheckPermission("system:user:update")
    @Idempotent
    @Operation(summary = "更新用户", description = "更新用户基础信息")
    public ResultResponse<UserResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody UserUpdateRequest request) {
        UserPO user = userApplicationService.update(id, request);
        return ResultResponse.success(responseVOConverter.toResponse(user));
    }

    @DeleteMapping("/{id}")
    @SaCheckPermission("system:user:delete")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        userApplicationService.delete(id);
        return ResultResponse.success();
    }

}
