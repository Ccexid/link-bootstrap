package me.link.bootstrap.interfaces.web.advice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.interfaces.dto.response.ResultResponse;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器，将参数异常、业务异常和系统异常转换为统一响应。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理BusinessException。
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResultResponse<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        log.warn("业务异常, uri: {}, errorCode: {}, message: {}",
                request.getRequestURI(), e.getErrorCode().getCode(), e.getMessage());
        return ResultResponse.failure(e.getErrorCode(), e.getMessage());
    }

    /**
     * 处理校验异常。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数校验异常, uri: {}, message: {}", request.getRequestURI(), message);

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, message);
    }

    /**
     * 处理BindException。
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleBindException(BindException e, HttpServletRequest request) {
        String message = e.getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        log.warn("参数绑定异常, uri: {}, message: {}", request.getRequestURI(), message);

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, message);
    }

    /**
     * 处理ConstraintViolationException。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleConstraintViolationException(ConstraintViolationException e, HttpServletRequest request) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        log.warn("约束校验异常, uri: {}, message: {}", request.getRequestURI(), message);

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, message);
    }

    /**
     * 处理IllegalArgumentException。
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleIllegalArgumentException(IllegalArgumentException e, HttpServletRequest request) {
        log.warn("非法参数异常, uri: {}, message: {}", request.getRequestURI(), e.getMessage());

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR, e.getMessage());
    }

    /**
     * 处理MissingParamException。
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResultResponse<Void> handleMissingParamException(MissingServletRequestParameterException e, HttpServletRequest request) {
        log.warn("缺少请求参数, uri: {}, param: {}",
                request.getRequestURI(), e.getParameterName());

        return ResultResponse.failure(ErrorCode.PARAM_VALIDATION_ERROR,
                "缺少必要参数: " + e.getParameterName());
    }

    /**
     * 处理认证Exception。
     */
    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResultResponse<Void> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("认证异常, uri: {}, message: {}", request.getRequestURI(), e.getMessage());
        return ResultResponse.failure(ErrorCode.UNAUTHORIZED, "未登录或登录已失效");
    }

    /**
     * 处理访问DeniedException。
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResultResponse<Void> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("权限不足异常, uri: {}, message: {}", request.getRequestURI(), e.getMessage());
        return ResultResponse.failure(ErrorCode.FORBIDDEN, "无权访问该资源");
    }

    /**
     * 处理MethodNotSupportedException。
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ResultResponse<Void> handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        log.warn("请求方法不支持, uri: {}, method: {}",
                request.getRequestURI(), e.getMethod());

        return ResultResponse.failure(ErrorCode.METHOD_NOT_ALLOWED,
                "不支持的请求方法: " + e.getMethod());
    }

    /**
     * 处理NoResourceFoundException。
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResultResponse<Void> handleNoResourceFoundException(NoResourceFoundException e, HttpServletRequest request) {
        log.warn("资源不存在, uri: {}", request.getRequestURI());

        return ResultResponse.failure(ErrorCode.NOT_FOUND, "请求的资源不存在");
    }

    /**
     * 处理异常。
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResultResponse<Void> handleException(Exception e, HttpServletRequest request) {
        log.error("系统异常, uri: {}", request.getRequestURI(), e);

        return ResultResponse.failure(ErrorCode.SYSTEM_ERROR);
    }
}
