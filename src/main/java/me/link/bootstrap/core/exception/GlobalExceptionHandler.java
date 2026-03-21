package me.link.bootstrap.core.exception;

import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.core.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 捕获我们手动抛出的业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn(">>> 业务异常拦截 [{}]: {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 捕获所有未知的系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleDefaultException(Exception e, HttpServletRequest request) {
        log.error(">>> 系统崩溃异常 URL: {}", request.getRequestURI(), e);
        // 生产环境不建议将 e.getMessage() 直接返回，防止暴露数据库表名等敏感信息
        return Result.error(500, "服务器出了点小问题，请稍后再试");
    }
}