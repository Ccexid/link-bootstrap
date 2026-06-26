package me.link.bootstrap.infrastructure.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.shared.kernel.annotation.RateLimit;
import me.link.bootstrap.shared.kernel.config.ClientIpProperties;
import me.link.bootstrap.shared.kernel.exception.BusinessException;
import me.link.bootstrap.shared.kernel.exception.ErrorCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.concurrent.TimeUnit;

/**
 * 通用限流切面，基于 Redis 固定窗口计数拦截高频请求。
 */
@Slf4j
@Aspect
@Component
@Order(-110)
@RequiredArgsConstructor
public class RateLimitAspect {

    private static final String KEY_PREFIX = "link:rate-limit:";

    private final RedissonClient redissonClient;
    private final ClientIpProperties clientIpProperties;
    private final ExpressionParser expressionParser = new SpelExpressionParser();
    private final ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        HttpServletRequest request = currentRequest();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String rawKey = rawKey(joinPoint, method, request, rateLimit);
        String key = KEY_PREFIX + sha256(rawKey);

        RAtomicLong counter = redissonClient.getAtomicLong(key);
        long current = counter.incrementAndGet();
        if (current == 1L) {
            counter.expire(rateLimit.windowSeconds(), TimeUnit.SECONDS);
        }
        if (current > rateLimit.maxRequests()) {
            log.warn("请求触发限流: method={}, uri={}, key={}, current={}, max={}, windowSeconds={}",
                    request == null ? method.getName() : request.getMethod(),
                    request == null ? method.toGenericString() : request.getRequestURI(),
                    key,
                    current,
                    rateLimit.maxRequests(),
                    rateLimit.windowSeconds());
            throw new BusinessException(ErrorCode.RATE_LIMITED, rateLimit.message());
        }

        return joinPoint.proceed();
    }

    private HttpServletRequest currentRequest() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes.getRequest();
    }

    private String rawKey(ProceedingJoinPoint joinPoint, Method method, HttpServletRequest request, RateLimit rateLimit) {
        String businessKey = trimToNull(rateLimit.key());
        if (businessKey != null) {
            return method.toGenericString() + ":spel=" + parseKey(businessKey, method, joinPoint.getArgs());
        }
        String httpMethod = request == null ? "" : request.getMethod();
        String uri = request == null ? method.toGenericString() : request.getRequestURI();
        String clientIp = request == null ? "" : clientIp(request);
        return httpMethod + ":" + uri + ":ip=" + clientIp;
    }

    private String parseKey(String expression, Method method, Object[] args) {
        EvaluationContext context = new StandardEvaluationContext();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
        if (parameterNames != null) {
            for (int i = 0; i < parameterNames.length; i++) {
                context.setVariable(parameterNames[i], args[i]);
            }
        }
        context.setVariable("args", args);
        Object value = expressionParser.parseExpression(expression).getValue(context);
        return value == null ? "" : value.toString();
    }

    private String clientIp(HttpServletRequest request) {
        String ip = null;
        if (clientIpProperties.isTrustForwardHeaders()) {
            for (String headerName : clientIpProperties.getForwardHeaders()) {
                String headerValue = request.getHeader(headerName);
                if (isNotBlank(headerValue)) {
                    ip = pickRightmostIp(headerValue);
                    break;
                }
            }
        }
        if (!isNotBlank(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip == null ? "" : ip;
    }

    private String pickRightmostIp(String headerValue) {
        int lastComma = headerValue.lastIndexOf(',');
        String candidate = lastComma >= 0 ? headerValue.substring(lastComma + 1) : headerValue;
        return candidate.trim();
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException ex) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, ex);
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
