package me.link.bootstrap.core.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 高性能时钟工具类
 * <p>
 * 主要作用：解决在高并发场景下频繁调用 {@code System.currentTimeMillis()} 可能带来的性能瓶颈。
 * 实现原理：通过一个独立的守护线程定期更新当前时间戳，读取时直接获取内存中的原子变量值，
 * 避免了频繁的系统调用（Native Method），从而提升性能。
 */
public class SystemClock {

    /**
     * 时间更新的间隔周期（单位：毫秒）
     * 默认设置为 1 毫秒，确保时间精度尽可能高
     */
    private final long period;

    /**
     * 存储当前时间戳的原子长整型变量
     * 使用 {@link AtomicLong} 保证多线程环境下的读写可见性和原子性，无需额外加锁
     */
    private final AtomicLong now;

    /**
     * 私有构造函数，防止外部实例化
     *
     * @param period 时间更新周期（毫秒）
     */
    private SystemClock(long period) {
        this.period = period;
        // 初始化当前时间为系统当前时间
        this.now = new AtomicLong(System.currentTimeMillis());
        // 启动定时任务线程，开始周期性更新时间
        scheduleClockUpdating();
    }

    /**
     * 静态内部类持有单例实例
     * <p>
     * 利用类加载机制保证线程安全的懒汉式单例模式：
     * 只有当第一次调用 {@code now()} 方法时，才会加载 {@code InstanceHolder} 类，
     * 进而初始化 {@code INSTANCE} 实例。
     */
    private static class InstanceHolder {
        /**
         * 单例实例，更新周期默认为 1 毫秒
         */
        private static final SystemClock INSTANCE = new SystemClock(1);
    }

    /**
     * 获取当前高精度时间戳
     * <p>
     * 该方法直接返回内存中已更新的时间戳，避免了系统调用开销，适用于高并发场景。
     *
     * @return 当前时间戳（毫秒）
     */
    public static long now() {
        return InstanceHolder.INSTANCE.now.get();
    }

    /**
     * 调度时钟更新任务
     * <p>
     * 创建一个单线程的调度执行器，启动一个守护线程，
     * 按照指定的周期（{@code period}）不断将最新系统时间写入 {@code now} 变量中。
     */
    private void scheduleClockUpdating() {
        // 创建单线程调度器，并自定义线程工厂以设置线程名称和守护状态
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "SystemClock-Updater");
            // 设置为守护线程，当主程序退出时，该线程会自动终止，不会阻止 JVM 关闭
            thread.setDaemon(true);
            return thread;
        });

        // 按固定频率执行更新任务：初始延迟为 period，之后每隔 period 毫秒执行一次
        // 任务内容：获取系统当前时间并更新到原子变量 now 中
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), period, period, TimeUnit.MILLISECONDS);
    }
}