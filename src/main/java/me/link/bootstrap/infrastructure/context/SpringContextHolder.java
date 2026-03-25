package me.link.bootstrap.infrastructure.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * Spring 上下文持有者
 * 优化点：增强健壮性校验、支持按名称获取、支持事件发布、增加状态检查
 */
@Slf4j
@Component
public class SpringContextHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        if (SpringContextHolder.applicationContext != null) {
            log.warn("SpringContextHolder 中的 ApplicationContext 被覆盖!");
        }
        SpringContextHolder.applicationContext = applicationContext;
        log.info("ApplicationContext 注入成功，可以通过 SpringContextHolder 获取 Bean");
    }

    /**
     * 获取 ApplicationContext
     */
    public static ApplicationContext getApplicationContext() {
        assertContextInjected();
        return applicationContext;
    }

    /**
     * 按类型获取 Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(clazz);
    }

    /**
     * 按名称获取 Bean
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        assertContextInjected();
        return (T) applicationContext.getBean(name);
    }

    /**
     * 按名称和类型获取 Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBean(name, clazz);
    }

    /**
     * 获取指定类型的所有 Bean (常用于策略模式)
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        assertContextInjected();
        return applicationContext.getBeansOfType(clazz);
    }

    /**
     * 发布 Spring 事件
     * 场景：解耦业务逻辑（如操作日志持久化事件）
     */
    public static void publishEvent(ApplicationEvent event) {
        assertContextInjected();
        applicationContext.publishEvent(event);
    }

    /**
     * 检查上下文是否已注入
     */
    private static void assertContextInjected() {
        Assert.state(applicationContext != null,
                "applicationContext 尚未注入，请在 spring-context.xml 中定义 SpringContextHolder 或在 class 上标注 @Component");
    }

    /**
     * 清理上下文（通常用于单元测试）
     */
    public static void clearHolder() {
        applicationContext = null;
    }
}