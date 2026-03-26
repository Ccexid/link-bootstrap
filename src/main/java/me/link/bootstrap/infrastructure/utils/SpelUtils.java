package me.link.bootstrap.infrastructure.utils;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 高性能 SpEL 表达式处理器 (增强版)
 * 优化点：
 * 1. 智能识别模板语法与原始语法
 * 2. 线程安全的表达式缓存
 * 3. 健壮的异常降级机制
 * 4. 支持方法参数名发现与额外变量注入
 */
@Slf4j
public class SpelUtils {

    private static final ExpressionParser PARSER = new SpelExpressionParser();
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 默认模板上下文，匹配 #{...} 格式
     */
    private static final ParserContext TEMPLATE_CONTEXT = new TemplateParserContext();

    /**
     * Expression 缓存：避免重复解析字符串为表达式对象
     */
    private static final Map<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>(256);

    /**
     * 解析 AOP 场景下的 SpEL 表达式
     * * @param joinPoint 切点信息
     * @param spel      表达式字符串。支持 "#{ #user.name }" (模板) 或 "#user.name" (纯表达式)
     * @param variables 额外注入的变量 (如 #result)
     * @return 解析后的字符串结果
     */
    public static String parse(JoinPoint joinPoint, String spel, @Nullable Map<String, Object> variables) {
        if (StrUtil.isBlank(spel)) {
            return StrUtil.EMPTY;
        }

        try {
            // 获取或创建缓存的表达式
            Expression expression = getExpression(spel);

            // 构建上下文：自动绑定方法参数名 (如 #dto, #id)
            EvaluationContext context = getContext(joinPoint, variables);

            Object value = expression.getValue(context);
            return value == null ? StrUtil.EMPTY : String.valueOf(value);
        } catch (Exception e) {
            // 生产环境审计日志不能因为解析报错影响主业务
            log.warn("SpEL解析异常 [{}], 表达式: {}, 报错信息: {}",
                    joinPoint.getSignature().getName(), spel, e.getMessage());
            return spel;
        }
    }

    /**
     * 简单场景解析 (非AOP)
     */
    public static String parseSimple(String spel, @Nullable Map<String, Object> variables) {
        if (StrUtil.isBlank(spel)) return StrUtil.EMPTY;
        try {
            Expression expression = getExpression(spel);
            EvaluationContext context = new StandardEvaluationContext();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }
            Object value = expression.getValue(context);
            return value == null ? StrUtil.EMPTY : String.valueOf(value);
        } catch (Exception e) {
            return spel;
        }
    }

    /**
     * 智能获取 Expression 实例
     */
    private static Expression getExpression(String spel) {
        return EXPRESSION_CACHE.computeIfAbsent(spel, key -> {
            // 如果包含模板标记，则使用模板解析器
            if (key.contains("#{")) {
                return PARSER.parseExpression(key, TEMPLATE_CONTEXT);
            }
            return PARSER.parseExpression(key);
        });
    }

    /**
     * 构建 EvaluationContext
     * MethodBasedEvaluationContext 是 Spring 专门为 AOP 方法参数解析准备的类
     */
    private static EvaluationContext getContext(JoinPoint joinPoint, Map<String, Object> variables) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 这里的 getTarget() 提供了 root object，让 SpEL 可以直接访问目标类的方法
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, args, NAME_DISCOVERER);

        // 注入额外变量。例如：#result, #oldData, #newData
        if (variables != null && !variables.isEmpty()) {
            variables.forEach(context::setVariable);
        }

        return context;
    }
}