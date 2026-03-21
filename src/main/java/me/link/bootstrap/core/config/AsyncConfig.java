package me.link.bootstrap.core.config;

import me.link.bootstrap.core.tenant.TenantContextHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务配置类
 * 用于配置支持租户上下文传递的异步线程池
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 创建租户感知的异步线程池 Bean
     * 该线程池能够自动将主线程中的租户上下文传递到异步执行线程中
     * 
     * @return 配置好的 Executor 实例
     */
    @Bean(name = "tenantAwareExecutor")
    public Executor tenantAwareExecutor() {
        // 创建 Spring 提供的线程池任务执行器
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 设置核心线程数：线程池中保持活跃的最小线程数量
        executor.setCorePoolSize(10);
        
        // 设置最大线程数：线程池允许创建的最大线程数量，超出核心线程数的线程会在空闲后被回收
        executor.setMaxPoolSize(50);
        
        // 设置队列容量：用于缓存待执行任务的阻塞队列大小，当核心线程都在忙碌时，新任务会进入队列
        executor.setQueueCapacity(200);
        
        // 设置线程名称前缀：便于在日志或监控中识别由该线程池创建的线程
        executor.setThreadNamePrefix("tenant-task-");

        // 【核心功能】设置任务装饰器
        // 作用：在任务提交到线程池执行前，对任务进行包装，实现从主线程到异步线程的上下文（如租户 ID）拷贝
        executor.setTaskDecorator(new TenantContextDecorator());

        // 设置拒绝策略：当线程池和队列都满时，由提交任务的调用者线程直接执行该任务
        // 目的：防止任务因资源不足而被丢弃，保证业务逻辑的最终执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 初始化线程池，使其生效
        executor.initialize();
        
        return executor;
    }

    /**
     * 租户上下文任务装饰器
     * 实现了 Spring 的 TaskDecorator 接口，用于在线程切换时传递上下文信息
     */
    static class TenantContextDecorator implements TaskDecorator {
        
        /**
         * 装饰 runnable 任务，使其在执行时携带主线程的租户上下文
         * 
         * @param runnable 原始的可执行任务
         * @return 包装后的 Runnable，包含上下文传递逻辑
         */
        @Override
        @NonNull
        public Runnable decorate(@NonNull Runnable runnable) {
            // 步骤 1: [在主线程中执行]
            // 从当前主线程的 ThreadLocal 中提取租户 ID
            // 此时获取的是发起异步调用时的租户环境
            String tenantId = TenantContextHolder.getTenantId();

            // 返回一个新的 Runnable，该 Runnable 将在异步线程中执行
            return () -> {
                try {
                    // 步骤 2: [在异步线程中执行]
                    // 将主线程提取到的租户 ID 设置到当前异步线程的 ThreadLocal 中
                    // 这样，异步执行的业务代码就能通过 TenantContextHolder 获取到正确的租户 ID
                    TenantContextHolder.setTenantId(tenantId);
                    
                    // 执行原始的业务逻辑
                    runnable.run();
                } finally {
                    // 步骤 3: [在异步线程中执行，无论是否异常都会执行]
                    // 任务执行完毕后，必须清理当前线程的租户上下文
                    // 原因：线程池中的线程是复用的，如果不清理，后续复用到该线程的其他任务可能会错误地继承之前的租户信息，导致数据污染
                    TenantContextHolder.clear();
                }
            };
        }
    }
}