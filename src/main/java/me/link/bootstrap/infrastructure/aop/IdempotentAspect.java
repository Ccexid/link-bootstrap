package me.link.bootstrap.infrastructure.aop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.shared.kernel.annotation.Idempotent;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import me.link.bootstrap.shared.kernel.util.SecurityHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性校验切面，基于 Redis 短期保留请求指纹，拦截重复提交。
 */
@Slf4j
@Aspect
@Component
@Order(-100)
@RequiredArgsConstructor
public class IdempotentAspect {

    private static final String KEY_PREFIX = "link:idempotent:";

    private final RedissonClient redissonClient;
    private final ObjectMapper objectMapper;

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        HttpServletRequest request = currentRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String fingerprint = fingerprint(joinPoint, method, request, idempotent);
        String key = KEY_PREFIX + fingerprint;
        RBucket<String> bucket = redissonClient.getBucket(key);
        boolean acquired = bucket.trySet(String.valueOf(System.currentTimeMillis()),
                idempotent.timeoutSeconds(), TimeUnit.SECONDS);
        if (!acquired) {
            log.warn("重复提交被拦截: method={}, uri={}, key={}",
                    request == null ? method.getName() : request.getMethod(),
                    request == null ? method.toGenericString() : request.getRequestURI(),
                    key);
            throw new BusinessException(ErrorCode.IDEMPOTENT_REPEATED_REQUEST, idempotent.message());
        }

        boolean success = false;
        try {
            Object result = joinPoint.proceed();
            success = true;
            return result;
        } finally {
            if (!success) {
                bucket.delete();
            }
        }
    }

    private HttpServletRequest currentRequest() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes.getRequest();
    }

    private String fingerprint(ProceedingJoinPoint joinPoint,
                               Method method,
                               HttpServletRequest request,
                               Idempotent idempotent) {
        String clientKey = request == null ? null : trimToNull(request.getHeader(idempotent.headerName()));
        String raw = clientKey != null ? "client:" + clientKey : "auto:" + autoFingerprint(joinPoint, method, request);
        return sha256(raw);
    }

    private String autoFingerprint(ProceedingJoinPoint joinPoint, Method method, HttpServletRequest request) {
        String httpMethod = request == null ? "" : request.getMethod().toUpperCase(Locale.ROOT);
        String uri = request == null ? "" : request.getRequestURI();
        String query = request == null ? "" : nullToEmpty(request.getQueryString());
        return httpMethod + ":" + uri + ":" + query
                + ":tenant=" + nullToEmpty(SecurityHelper.getTenantId())
                + ":user=" + nullToEmpty(SecurityHelper.getUserId())
                + ":method=" + method.toGenericString()
                + ":args=" + serializeArgs(joinPoint.getArgs());
    }

    private String serializeArgs(Object[] args) {
        List<Object> serializableArgs = Arrays.stream(args)
                .filter(arg -> !(arg instanceof ServletRequest))
                .filter(arg -> !(arg instanceof ServletResponse))
                .filter(arg -> !(arg instanceof MultipartFile))
                .toList();
        try {
            return objectMapper.writeValueAsString(serializableArgs);
        } catch (JsonProcessingException ex) {
            return serializableArgs.toString();
        }
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", ex);
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String nullToEmpty(Object value) {
        return value == null ? "" : value.toString();
    }
}
