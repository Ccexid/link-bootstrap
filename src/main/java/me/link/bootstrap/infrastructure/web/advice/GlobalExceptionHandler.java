package me.link.bootstrap.infrastructure.web.advice;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.infrastructure.tracing.TraceIdContext;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.interfaces.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultResponse<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常, uri: {}, errorCode: {}, message: {}",
                request.getRequestURI(), e.getErrorCode().getCode(), e.getMessage());
        return ResultResponse.failure(e.getErrorCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数校验异常,  uri: {}, message: {}", request.getRequestURI(), message);

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, message);
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数绑定异常, uri: {}, message: {}", request.getRequestURI(), message);

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("约束校验异常, traceId: {}, uri: {}, message: {}",
                TraceIdContext.get(), request.getRequestURI(), message);

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleMissingParamException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数,  uri: {}, param: {}",
                request.getRequestURI(), e.getParameterName());

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR,
                "缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(NotLoginException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultResponse<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        log.warn("未登录异常, uri: {}, type: {}",
                request.getRequestURI(), e.getType());

        String message = switch (e.getType()) {
            case NotLoginException.NOT_TOKEN -> "未提供认证令牌";
            case NotLoginException.INVALID_TOKEN -> "认证令牌无效";
            case NotLoginException.TOKEN_TIMEOUT -> "认证令牌已过期";
            case NotLoginException.BE_REPLACED -> "账号已在其他设备登录";
            case NotLoginException.KICK_OUT -> "账号已被强制下线";
            default -> "未登录或登录已失效";
        };

        return ResultResponse.failure(ErrorCode.UNAUTHORIZED, message);
    }

    @ExceptionHandler(NotPermissionException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultResponse<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        log.warn("权限不足异常,  uri: {}, permission: {}",
                request.getRequestURI(), e.getPermission());

        return ResultResponse.failure(ErrorCode.FORBIDDEN, "无权访问该资源");
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResultResponse<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持, uri: {}, method: {}",
                request.getRequestURI(), e.getMethod());

        return ResultResponse.failure(ErrorCode.METHOD_NOT_ALLOWED,
                "不支持的请求方法: " + e.getMethod());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultResponse<Void> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("资源不存在,uri: {}", request.getRequestURI());

        return ResultResponse.failure(ErrorCode.NOT_FOUND, "请求的资源不存在");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultResponse<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常,  uri: {}",
                request.getRequestURI(), e);

        return ResultResponse.failure(ErrorCode.SYSTEM_ERROR);
    }
}
