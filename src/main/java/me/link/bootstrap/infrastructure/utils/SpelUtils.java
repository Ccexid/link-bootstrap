package me.link.bootstrap.infrastructure.utils;

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
 * 高性能 SpEL 表达式处理器
 * 优化点：支持模板解析、使用 MethodBasedEvaluationContext、强化缓存逻辑
 */
public class SpelUtils {

    /**
     * 解析器实例 (线程安全)
     */
    private static final ExpressionParser PARSER = new SpelExpressionParser();

    /**
     * 参数名发现器 (用于获取方法参数名称)
     */
    private static final ParameterNameDiscoverer NAME_DISCOVERER = new DefaultParameterNameDiscoverer();

    /**
     * 模板解析上下文：识别 #{...} 这种格式，方便混合文本解析
     */
    private static final ParserContext TEMPLATE_CONTEXT = new TemplateParserContext();

    /**
     * Expression 缓存
     */
    private static final Map<String, Expression> EXPRESSION_CACHE = new ConcurrentHashMap<>(256);

    /**
     * 解析 SpEL 表达式 (支持混合文本模板)
     * 示例：parse(jp, "ID为 #{#id} 的用户已更新", null)
     */
    public static String parse(JoinPoint joinPoint, String spel, @Nullable Map<String, Object> variables) {
        if (spel == null || spel.isBlank()) {
            return "";
        }

        try {
            // 1. 判断是否包含模板语法 #{}，如果不包含则按普通 SpEL 处理
            boolean isTemplate = spel.contains("#{");
            Expression expression = EXPRESSION_CACHE.computeIfAbsent(spel,
                    key -> isTemplate ? PARSER.parseExpression(key, TEMPLATE_CONTEXT) : PARSER.parseExpression(key));

            // 2. 构建 Context
            EvaluationContext context = getContext(joinPoint, variables);

            // 3. 执行
            Object value = expression.getValue(context);
            return value != null ? value.toString() : "";
        } catch (Exception e) {
            // 降级处理：解析失败返回原字符串，防止审计日志报错导致业务回滚
            return spel;
        }
    }

    /**
     * 构建 EvaluationContext
     * 使用 MethodBasedEvaluationContext 能更好地处理方法参数绑定
     */
    private static EvaluationContext getContext(JoinPoint joinPoint, Map<String, Object> variables) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        Object[] args = joinPoint.getArgs();

        // 针对 AOP 方法参数解析优化的 Context
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                joinPoint.getTarget(), method, args, NAME_DISCOVERER);

        // 注入额外的变量 (例如 #result, #errorMsg)
        if (variables != null && !variables.isEmpty()) {
            variables.forEach(context::setVariable);
        }

        return context;
    }

    /**
     * 简单的非 AOP 场景解析
     */
    public static String parseSimple(String spel, Map<String, Object> variables) {
        if (spel == null || spel.isBlank()) return "";
        try {
            Expression expression = EXPRESSION_CACHE.computeIfAbsent(spel, PARSER::parseExpression);
            EvaluationContext context = new StandardEvaluationContext();
            variables.forEach(context::setVariable);
            return String.valueOf(expression.getValue(context));
        } catch (Exception e) {
            return spel;
        }
    }
}