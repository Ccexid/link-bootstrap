package me.link.bootstrap.infrastructure.aop;

import cn.dev33.satoken.stp.StpUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.link.bootstrap.application.command.CreateOperateLogCommand;
import me.link.bootstrap.application.service.OperateLogApplicationService;
import me.link.bootstrap.infrastructure.tracing.TraceIdContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import static me.link.bootstrap.shared.kernel.constant.GlobalConstants.SYSTEM_USER;

/**
 * 操作日志切面，负责为所有 Controller 接口自动记录访问日志。
 *
 * <p>该切面统一采集接口模块、动作、请求信息、当前用户、链路 ID、执行耗时和成功状态，
 * 让新增或已有接口无需手动调用操作日志服务即可自动落库。</p>
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperateLogAspect {

    /** 无法识别具体业务 ID 时使用的默认业务标识。 */
    private static final long DEFAULT_BIZ_ID = 0L;

    /** 默认用户类型，当前项目暂未建立统一用户类型上下文。 */
    private static final int DEFAULT_USER_TYPE = 0;

    /** GET 请求对应查询操作。 */
    private static final int OPERATION_QUERY = 1;

    /** POST 请求对应新增或执行操作。 */
    private static final int OPERATION_CREATE = 2;

    /** PUT/PATCH 请求对应更新操作。 */
    private static final int OPERATION_UPDATE = 3;

    /** DELETE 请求对应删除操作。 */
    private static final int OPERATION_DELETE = 4;

    /** 其他请求方法对应通用操作。 */
    private static final int OPERATION_OTHER = 0;

    /** 写入 extra 字段的内容长度上限，避免大请求体导致日志过大。 */
    private static final int MAX_EXTRA_LENGTH = 2000;

    private final OperateLogApplicationService operateLogApplicationService;
    private final ObjectMapper objectMapper;

    /**
     * 环绕所有 Controller 公共接口方法，自动记录成功和异常操作日志。
     *
     * @param joinPoint 当前被调用的 Controller 方法连接点
     * @return 原接口方法返回值
     * @throws Throwable 原接口方法抛出的异常
     */
    @Around("within(me.link.bootstrap.interfaces.controller..*) && execution(public * *(..))")
    public Object recordOperateLog(ProceedingJoinPoint joinPoint) throws Throwable {
        if (isOperateLogController(joinPoint)) {
            return joinPoint.proceed();
        }

        long startTime = System.currentTimeMillis();
        Throwable thrown = null;
        Object result = null;
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable ex) {
            thrown = ex;
            throw ex;
        } finally {
            int duration = safeDuration(startTime);
            record(joinPoint, thrown == null, thrown, duration);
        }
    }

    /**
     * 判断当前调用是否属于操作日志自身接口，避免记录日志时形成递归调用。
     *
     * @param joinPoint 当前 Controller 方法连接点
     * @return true 表示操作日志接口自身，false 表示普通业务接口
     */
    private boolean isOperateLogController(ProceedingJoinPoint joinPoint) {
        return joinPoint.getSignature().getDeclaringTypeName().endsWith("OperateLogController");
    }

    /**
     * 构造并保存操作日志，保存失败只写应用日志，不影响业务接口返回。
     *
     * @param joinPoint 当前 Controller 方法连接点
     * @param success   接口是否成功执行
     * @param thrown    接口执行异常，成功时为空
     * @param duration  接口执行耗时，单位毫秒
     */
    private void record(ProceedingJoinPoint joinPoint, boolean success, Throwable thrown, int duration) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return;
        }

        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            String requestMethod = request.getMethod();
            operateLogApplicationService.create(new CreateOperateLogCommand(
                    TraceIdContext.get(),
                    currentUserId(),
                    DEFAULT_USER_TYPE,
                    clientIp(request),
                    truncate(request.getHeader("User-Agent"), 512),
                    moduleName(joinPoint, method),
                    operationType(requestMethod),
                    bizId(joinPoint.getArgs()),
                    actionName(method),
                    extra(joinPoint, request, thrown),
                    success,
                    requestMethod,
                    truncate(request.getRequestURI(), 512),
                    duration
            ));
        } catch (Exception ex) {
            log.warn("自动记录操作日志失败: {}", ex.getMessage(), ex);
        }
    }

    /**
     * 获取当前 HTTP 请求对象。
     *
     * @return 当前请求对象，非 Web 请求场景返回 null
     */
    private HttpServletRequest currentRequest() {
        if (!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes.getRequest();
    }

    /**
     * 获取当前登录用户 ID，未登录或获取失败时使用系统用户。
     *
     * @return 当前用户 ID 或系统用户 ID
     */
    private Long currentUserId() {
        try {
            if (StpUtil.isLogin()) {
                return StpUtil.getLoginIdAsLong();
            }
        } catch (Exception ex) {
            log.debug("获取当前登录用户失败，使用系统用户记录操作日志: {}", ex.getMessage());
        }
        return SYSTEM_USER;
    }

    /**
     * 从请求头中提取客户端真实 IP。
     *
     * @param request HTTP 请求对象
     * @return 客户端 IP 地址
     */
    private String clientIp(HttpServletRequest request) {
        String ip = firstNotBlank(
                request.getHeader("X-Forwarded-For"),
                request.getHeader("X-Real-IP"),
                request.getHeader("Proxy-Client-IP"),
                request.getHeader("WL-Proxy-Client-IP"),
                request.getRemoteAddr()
        );
        if (ip != null && ip.contains(",")) {
            ip = ip.substring(0, ip.indexOf(',')).trim();
        }
        return truncate(ip, 128);
    }

    /**
     * 根据 Controller 的 Swagger 标签解析模块名称。
     *
     * @param joinPoint 当前 Controller 方法连接点
     * @param method    当前接口方法
     * @return 模块名称
     */
    private String moduleName(ProceedingJoinPoint joinPoint, Method method) {
        Tag methodTag = AnnotationUtils.findAnnotation(method, Tag.class);
        if (methodTag != null && isNotBlank(methodTag.name())) {
            return methodTag.name();
        }
        Tag classTag = AnnotationUtils.findAnnotation(joinPoint.getSignature().getDeclaringType(), Tag.class);
        if (classTag != null && isNotBlank(classTag.name())) {
            return classTag.name();
        }
        return joinPoint.getSignature().getDeclaringType().getSimpleName();
    }

    /**
     * 根据 Swagger Operation 注解解析动作名称。
     *
     * @param method 当前接口方法
     * @return 动作名称
     */
    private String actionName(Method method) {
        Operation operation = AnnotationUtils.findAnnotation(method, Operation.class);
        if (operation != null && isNotBlank(operation.summary())) {
            return operation.summary();
        }
        return method.getName();
    }

    /**
     * 根据 HTTP 方法映射操作类型。
     *
     * @param requestMethod HTTP 请求方法
     * @return 操作类型编码
     */
    private Integer operationType(String requestMethod) {
        if (requestMethod == null) {
            return OPERATION_OTHER;
        }
        return switch (requestMethod.toUpperCase(Locale.ROOT)) {
            case "GET" -> OPERATION_QUERY;
            case "POST" -> OPERATION_CREATE;
            case "PUT", "PATCH" -> OPERATION_UPDATE;
            case "DELETE" -> OPERATION_DELETE;
            default -> OPERATION_OTHER;
        };
    }

    /**
     * 从路径参数或请求对象中提取业务 ID，无法提取时返回默认业务 ID。
     *
     * @param args Controller 方法参数
     * @return 业务 ID
     */
    private Long bizId(Object[] args) {
        for (Object arg : args) {
            if (arg instanceof Long id && id >= 0) {
                return id;
            }
        }
        Long id = readLongProperty(args, "getId");
        return id == null ? DEFAULT_BIZ_ID : id;
    }

    /**
     * 从请求对象中提取租户 ID。
     *
     * @param args Controller 方法参数
     * @return 租户 ID，无法提取时返回 null
     */
    private Long tenantId(Object[] args) {
        return readLongProperty(args, "getTenantId");
    }

    /**
     * 反射读取参数对象中的 Long 类型属性。
     *
     * @param args       Controller 方法参数
     * @param getterName getter 方法名
     * @return 读取到的 Long 值，读取失败返回 null
     */
    private Long readLongProperty(Object[] args, String getterName) {
        for (Object arg : args) {
            if (arg == null || isFrameworkArgument(arg)) {
                continue;
            }
            try {
                Method getter = arg.getClass().getMethod(getterName);
                Object value = getter.invoke(arg);
                if (value instanceof Long longValue && longValue >= 0) {
                    return longValue;
                }
            } catch (Exception ignored) {
            }
        }
        return null;
    }

    /**
     * 构造日志扩展信息，包含脱敏后的请求参数和异常信息。
     *
     * @param joinPoint 当前 Controller 方法连接点
     * @param request   HTTP 请求对象
     * @param thrown    接口异常信息
     * @return JSON 格式扩展信息
     */
    private String extra(ProceedingJoinPoint joinPoint, HttpServletRequest request, Throwable thrown) {
        Map<String, Object> extra = new LinkedHashMap<>();
        extra.put("query", request.getQueryString());
        extra.put("args", sanitizedArgs(joinPoint));
        if (thrown != null) {
            extra.put("error", thrown.getClass().getSimpleName());
            extra.put("message", thrown.getMessage());
        }
        try {
            return truncate(objectMapper.writeValueAsString(extra), MAX_EXTRA_LENGTH);
        } catch (JsonProcessingException ex) {
            return truncate(extra.toString(), MAX_EXTRA_LENGTH);
        }
    }

    /**
     * 将 Controller 参数转换为可记录的脱敏参数 Map。
     *
     * @param joinPoint 当前 Controller 方法连接点
     * @return 参数名到参数值的映射
     */
    private Map<String, Object> sanitizedArgs(ProceedingJoinPoint joinPoint) {
        Map<String, Object> result = new LinkedHashMap<>();
        String[] names = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (isFrameworkArgument(arg)) {
                continue;
            }
            String name = names != null && i < names.length ? names[i] : "arg" + i;
            result.put(name, sanitizeValue(name, arg));
        }
        return result;
    }

    /**
     * 对敏感参数进行脱敏处理。
     *
     * @param name 参数名称
     * @param arg  参数值
     * @return 可安全写入日志的参数值
     */
    private Object sanitizeValue(String name, Object arg) {
        if (arg == null) {
            return null;
        }
        if (isSensitiveName(name)) {
            return "******";
        }
        try {
            String json = objectMapper.writeValueAsString(arg);
            Map<?, ?> map = objectMapper.readValue(json, Map.class);
            return sanitizeMap(map);
        } catch (Exception ignored) {
            return truncate(String.valueOf(arg), 512);
        }
    }

    /**
     * 递归脱敏 Map 中的敏感字段。
     *
     * @param source 原始 Map
     * @return 脱敏后的 Map
     */
    private Map<String, Object> sanitizeMap(Map<?, ?> source) {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            String key = String.valueOf(entry.getKey());
            Object value = entry.getValue();
            if (isSensitiveName(key)) {
                result.put(key, "******");
            } else if (value instanceof Map<?, ?> nested) {
                result.put(key, sanitizeMap(nested));
            } else {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 判断参数名是否属于敏感字段。
     *
     * @param name 参数名或字段名
     * @return true 表示需要脱敏
     */
    private boolean isSensitiveName(String name) {
        String lowerName = name == null ? "" : name.toLowerCase(Locale.ROOT);
        return lowerName.contains("password")
                || lowerName.contains("token")
                || lowerName.contains("authorization")
                || lowerName.contains("secret")
                || lowerName.contains("credential");
    }

    /**
     * 判断参数是否为框架对象，框架对象不写入业务操作日志。
     *
     * @param arg 方法参数
     * @return true 表示框架对象
     */
    private boolean isFrameworkArgument(Object arg) {
        return arg instanceof HttpServletRequest
                || arg instanceof MultipartFile
                || arg instanceof MultipartFile[];
    }

    /**
     * 计算安全的耗时毫秒值，避免超过 Integer 范围。
     *
     * @param startTime 开始时间戳
     * @return 耗时毫秒值
     */
    private int safeDuration(long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        return duration > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(duration, 0);
    }

    /**
     * 返回第一个非空白字符串。
     *
     * @param values 候选字符串
     * @return 第一个非空白字符串
     */
    private String firstNotBlank(String... values) {
        for (String value : values) {
            if (isNotBlank(value)) {
                return value;
            }
        }
        return "";
    }

    /**
     * 判断字符串是否包含非空白字符。
     *
     * @param value 待判断字符串
     * @return true 表示非空白
     */
    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }

    /**
     * 截断字符串到指定长度。
     *
     * @param value  原始字符串
     * @param length 最大长度
     * @return 截断后的字符串
     */
    private String truncate(String value, int length) {
        if (value == null || value.length() <= length) {
            return value;
        }
        return value.substring(0, length);
    }
}
