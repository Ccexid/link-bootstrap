package me.link.bootstrap.interfaces.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.link.bootstrap.application.service.AuthService;
import me.link.bootstrap.infrastructure.crypto.ApiCryptoProperties;
import me.link.bootstrap.interfaces.dto.request.auth.EmailLoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.LoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.SendEmailCodeRequest;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.dto.response.vo.ApiCryptoPublicKeyResponseVO;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;
import me.link.bootstrap.shared.kernel.annotation.RateLimit;
import me.link.bootstrap.shared.kernel.constant.GlobalConstants;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口。
 * <p>创建 Token、发送验证码、获取公钥等未登录入口必须在 SaTokenConfigure 的白名单内。</p>
 */
@RestController
@RequestMapping(GlobalConstants.API_PREFIX + "/auth")
@Validated
@RequiredArgsConstructor
@Tag(name = "认证接口", description = "登录、登出")
public class AuthController {

    private final AuthService authService;
    private final ApiCryptoProperties apiCryptoProperties;

    /**
     * 创建 Token(账号密码)。
     */
    @PostMapping("/tokens")
    @RateLimit(key = "#args[0].username", includeClientIp = true, windowSeconds = 60L, maxRequests = 5L, message = "账号登录尝试过于频繁,请稍后再试")
    @Operation(summary = "创建 Token(账号密码)", description = "校验账号密码并签发 Token")
    public ResultResponse<TokenResponseVO> login(@Valid @RequestBody LoginRequest request) {
        return ResultResponse.success(authService.login(request));
    }

    /**
     * 创建 Token(邮箱验证码)。
     */
    @PostMapping("/email-code-tokens")
    @RateLimit(key = "#args[0].email", includeClientIp = true, windowSeconds = 60L, maxRequests = 5L, message = "邮箱登录尝试过于频繁,请稍后再试")
    @Operation(summary = "创建 Token(邮箱验证码)", description = "校验邮箱验证码并签发 Token")
    public ResultResponse<TokenResponseVO> emailLogin(@Valid @RequestBody EmailLoginRequest request) {
        return ResultResponse.success(authService.emailLogin(request));
    }

    /**
     * 创建邮箱验证码。
     */
    @PostMapping("/email-verification-codes")
    @RateLimit(key = "#args[0].email", includeClientIp = true, windowSeconds = 60L, maxRequests = 1L, message = "验证码发送过于频繁,请稍后再试")
    @Operation(summary = "创建邮箱验证码", description = "向指定邮箱发送登录验证码")
    public ResultResponse<Void> sendEmailCode(@Valid @RequestBody SendEmailCodeRequest request) {
        authService.sendEmailCode(request);
        return ResultResponse.success();
    }

    /**
     * 刷新 Token。
     */
    @PutMapping("/tokens/current")
    @Operation(summary = "刷新 Token", description = "基于当前有效 Token 续期,并返回最新剩余有效期")
    public ResultResponse<TokenResponseVO> refreshToken() {
        return ResultResponse.success(authService.refreshToken());
    }

    /**
     * 查询当前 Token。
     */
    @GetMapping("/tokens/current")
    @Operation(summary = "查询当前 Token", description = "返回当前 Token 名称、值、前缀及剩余有效期")
    public ResultResponse<TokenResponseVO> currentToken() {
        return ResultResponse.success(authService.currentToken());
    }

    /**
     * 获取接口加密公钥。
     */
    @GetMapping("/public-keys/current")
    @Operation(summary = "获取接口加密公钥", description = "前端使用该公钥加密请求体 data 字段")
    public ResultResponse<ApiCryptoPublicKeyResponseVO> publicKey() {
        return ResultResponse.success(new ApiCryptoPublicKeyResponseVO(apiCryptoProperties.getPublicKey()));
    }

    /**
     * 删除当前 Token。
     */
    @DeleteMapping("/tokens/current")
    @Operation(summary = "删除当前 Token", description = "清除当前 Token 关联的会话")
    public ResultResponse<Void> logout() {
        authService.logout();
        return ResultResponse.success();
    }

}
