package me.link.bootstrap.application.service;

import java.util.List;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.dto.request.auth.EmailLoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.LoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.SendEmailCodeRequest;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;

public interface AuthService {

    /**
     * 执行用户名密码登录。
     */
    TokenResponseVO login(LoginRequest request);
    /**
     * 执行邮箱验证码登录。
     */
    TokenResponseVO emailLogin(EmailLoginRequest request);
    /**
     * 发送邮箱验证码。
     */
    void sendEmailCode(SendEmailCodeRequest request);
    /**
     * 刷新访问令牌。
     */
    TokenResponseVO refreshToken();
    /**
     * 查询当前登录令牌。
     */
    TokenResponseVO currentToken();
    /**
     * 注销当前登录会话。
     */
    void logout();
}
