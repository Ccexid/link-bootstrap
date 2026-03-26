package me.link.bootstrap.core.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 高性能时钟工具类
 * <p>
 * 规约：符合项目 Util 结尾的命名规范。
 * 作用：缓存系统时间戳，解决高并发下 System.currentTimeMillis() 的系统调用性能瓶颈。
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClockUtil {

    private static final AtomicLong NOW = new AtomicLong(System.currentTimeMillis());
    private static final ScheduledExecutorService SCHEDULER;

    static {
        // 初始化单线程调度器
        SCHEDULER = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "clock-util-updater");
            thread.setDaemon(true);
            return thread;
        });

        // 每隔 1 毫秒刷新一次内存中的时间戳
        SCHEDULER.scheduleAtFixedRate(
                () -> NOW.set(System.currentTimeMillis()),
                1, 1, TimeUnit.MILLISECONDS
        );

        // 注册钩子，确保应用关闭时线程池安全退出
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!SCHEDULER.isShutdown()) {
                SCHEDULER.shutdown();
                log.info("[ClockUtil] 已安全关闭时钟更新线程池");
            }
        }));
    }

    /**
     * 获取当前系统毫秒时间戳（从内存读取）
     * * @return 当前时间戳（ms）
     */
    public static long now() {
        return NOW.get();
    }

    /**
     * 获取当前时间戳的字符串形式（可选扩展）
     */
    public static String nowString() {
        return String.valueOf(now());
    }
}