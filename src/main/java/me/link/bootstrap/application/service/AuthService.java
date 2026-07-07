package me.link.bootstrap.application.service;

import java.util.List;
import me.link.bootstrap.infrastructure.persistence.po.UserPO;
import me.link.bootstrap.interfaces.dto.request.auth.EmailLoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.LoginRequest;
import me.link.bootstrap.interfaces.dto.request.auth.SendEmailCodeRequest;
import me.link.bootstrap.interfaces.dto.response.vo.TokenResponseVO;

public interface AuthService {

    TokenResponseVO login(LoginRequest request);
    TokenResponseVO emailLogin(EmailLoginRequest request);
    void sendEmailCode(SendEmailCodeRequest request);
    TokenResponseVO refreshToken();
    TokenResponseVO currentToken();
    void logout();
}
