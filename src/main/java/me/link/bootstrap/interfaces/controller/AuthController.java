package me.link.bootstrap.interfaces.controller;

import cn.dev33.satoken.stp.StpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.LoginCommand;
import me.link.bootstrap.application.service.AuthApplicationService;
import me.link.bootstrap.interfaces.dto.request.auth.LoginRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.vo.LoginResponseVO;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 * <p>登录、登出接口必须在 SaTokenConfigure 的白名单内,否则未登录用户无法访问 /auth/login。</p>
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "登录、登出")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验账号密码并签发 Token")
    public ResultResponse<LoginResponseVO> login(@Valid @RequestBody LoginRequest request) {
        String tokenValue = authApplicationService.login(new LoginCommand(
                request.getUsername(),
                request.getPassword(),
                request.getTenantId()
        ));
        return ResultResponse.success(new LoginResponseVO(StpUtil.getTokenName(), tokenValue));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "清除当前 Token 关联的会话")
    public ResultResponse<Void> logout() {
        authApplicationService.logout();
        return ResultResponse.success();
    }
}
