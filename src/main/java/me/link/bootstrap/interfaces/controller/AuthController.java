package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.command.LoginCommand;
import me.link.bootstrap.application.service.AuthApplicationService;
import me.link.bootstrap.interfaces.converter.ResponseVOConverter;
import me.link.bootstrap.interfaces.dto.request.auth.LoginRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
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
    private final ResponseVOConverter responseVOConverter;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验账号密码并签发 Token")
    public ResultResponse<TokenResponseVO> login(@Valid @RequestBody LoginRequest request) {
        authApplicationService.login(new LoginCommand(
                request.getUsername(),
                request.getPassword(),
                request.getTenantId()
        ));
        return ResultResponse.success(responseVOConverter.toResponse(authApplicationService.currentToken()));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "刷新 Token", description = "基于当前有效 Token 续期,并返回最新剩余有效期")
    public ResultResponse<TokenResponseVO> refreshToken() {
        return ResultResponse.success(responseVOConverter.toResponse(authApplicationService.refreshToken()));
    }

    @GetMapping("/token")
    @Operation(summary = "查询当前 Token", description = "返回当前 Token 名称、值、前缀及剩余有效期")
    public ResultResponse<TokenResponseVO> currentToken() {
        return ResultResponse.success(responseVOConverter.toResponse(authApplicationService.currentToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "清除当前 Token 关联的会话")
    public ResultResponse<Void> logout() {
        authApplicationService.logout();
        return ResultResponse.success();
    }

}
