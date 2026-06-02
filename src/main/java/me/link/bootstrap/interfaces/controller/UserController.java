package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.CreateUserCommand;
import me.link.bootstrap.application.command.UserPageQuery;
import me.link.bootstrap.application.command.UpdateUserCommand;
import me.link.bootstrap.application.service.UserApplicationService;
import me.link.bootstrap.domain.entity.UserEntity;
import me.link.bootstrap.domain.valueobject.PageResult;
import me.link.bootstrap.interfaces.dto.request.user.UserCreateRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserPageRequest;
import me.link.bootstrap.interfaces.dto.request.user.UserUpdateRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.ResultTableResponse;
import me.link.bootstrap.interfaces.dto.response.vo.UserResponseVO;
import me.link.bootstrap.interfaces.validation.SortWhitelist;
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

import java.util.List;

@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/system/users")
@Validated
@RequiredArgsConstructor
@Tag(name = "用户接口", description = "用户增删改查接口")
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    @Operation(summary = "创建用户", description = "创建用户基础信息")
    public ResultResponse<UserResponseVO> create(@Valid @RequestBody UserCreateRequest request) {
        UserEntity user = userApplicationService.create(new CreateUserCommand(
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getUserType(),
                request.getMobile(),
                request.getAvatar(),
                request.getStatus(),
                request.getOrgId(),
                request.getDeptId(),
                request.getLoginIp(),
                request.getLoginDate()
        ));
        return ResultResponse.success(toResponse(user));
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询用户详情", description = "根据ID查询用户详情")
    public ResultResponse<UserResponseVO> get(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        return ResultResponse.success(toResponse(userApplicationService.get(id)));
    }

    @GetMapping
    @Operation(summary = "分页查询用户", description = "分页查询用户列表")
    public ResultTableResponse<UserResponseVO> page(@Validated @SortWhitelist(UserResponseVO.class) UserPageRequest request) {
        PageResult<UserEntity> pageResult = userApplicationService.page(new UserPageQuery(
                request.getPageNo(),
                request.getPageSize(),
                request.getUsername(),
                request.getNickname(),
                request.getMobile(),
                request.getUserType(),
                request.getStatus(),
                request.getSortingFields()
        ));
        List<UserResponseVO> records = pageResult.records().stream()
                .map(this::toResponse)
                .toList();
        return ResultTableResponse.success(records, pageResult.total());
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新用户", description = "更新用户基础信息")
    public ResultResponse<UserResponseVO> update(@PathVariable @NotNull(message = "ID不能为空") Long id,
                                                  @Valid @RequestBody UserUpdateRequest request) {
        UserEntity user = userApplicationService.update(new UpdateUserCommand(
                id,
                request.getUsername(),
                request.getPassword(),
                request.getNickname(),
                request.getUserType(),
                request.getMobile(),
                request.getAvatar(),
                request.getStatus(),
                request.getOrgId(),
                request.getDeptId(),
                request.getLoginIp(),
                request.getLoginDate()
        ));
        return ResultResponse.success(toResponse(user));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除用户", description = "根据ID删除用户")
    public ResultResponse<Void> delete(@PathVariable @NotNull(message = "ID不能为空") Long id) {
        userApplicationService.delete(id);
        return ResultResponse.success();
    }

    private UserResponseVO toResponse(UserEntity user) {
        UserResponseVO response = new UserResponseVO();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setUserType(user.getUserType());
        response.setMobile(user.getMobile());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setOrgId(user.getOrgId());
        response.setDeptId(user.getDeptId());
        response.setLoginIp(user.getLoginIp());
        response.setLoginDate(user.getLoginDate());
        response.setTenantId(user.getTenantId());
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
