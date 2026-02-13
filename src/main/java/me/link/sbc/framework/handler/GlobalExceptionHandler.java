package me.link.sbc.framework.handler;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import me.link.sbc.common.api.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(NotLoginException.class)
    public Result<Void> handlerNotLoginException(NotLoginException nle) {
        // 打印堆栈或日志
        String msg = switch (nle.getType()) {
            case NotLoginException.TOKEN_TIMEOUT -> "登录已过期，请重新登录";
            case NotLoginException.BE_REPLACED -> "账号已在其他设备登录，您已被迫下线";
            case NotLoginException.KICK_OUT -> "您已被管理员强制下线";
            default -> "当前会话未登录，请登录";
        };
        return Result.fail(msg);
    }

    @ExceptionHandler(NotPermissionException.class)
    public Result<Void> handlerNotPermissionException() {
        return Result.fail("对不起，您没有操作该功能的权限");
    }
}
