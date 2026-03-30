package me.link.bootstrap.util;

import cn.hutool.core.util.IdUtil;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 分布式 ID 生成工具类，继承自 Hutool 的 IdUtil。
 * <p>
 * 该类利用 Redis (Redisson) 实现全局唯一且有序的序列号生成，支持按天重置序列或全局累加模式。
 * 生成的 ID 格式为：前缀 + 日期 (可选) + 指定位数的自增序列。
 * </p>
 *
 * @author link
 */
@Component
@RequiredArgsConstructor
public class IdUtils extends IdUtil {

    /**
     * Redisson 客户端实例，用于操作分布式原子长整型计数器。
     */
    private final RedissonClient redissonClient;

    /**
     * 日期格式化器，格式为 "yyyyMMdd"。
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * Redis Key 的基础前缀。
     */
    private static final String BASE_PREFIX = "id_gen:";

    /**
     * 静态实例引用，用于在静态方法中访问非静态成员（通过 @PostConstruct 初始化）。
     */
    private static IdUtils instance;

    /**
     * Spring 容器启动后初始化静态实例。
     */
    @PostConstruct
    public void init() {
        instance = this;
    }

    /**
     * 获取下一个分布式唯一 ID 的静态入口方法。
     *
     * @param prefix  ID 的前缀标识，用于区分不同业务类型。
     * @param digit   自增序列号的位数（不足部分补零）。
     * @param isDaily 是否按天重置序列号。
     *                - true: 每天从 1 开始计数，生成的 ID 包含日期字符串。
     *                - false: 全局累加计数，生成的 ID 不包含日期字符串。
     * @return 生成的唯一 ID 字符串。
     */
    public static String getNextId(String prefix, int digit, boolean isDaily) {
        return instance.nextId(prefix, digit, isDaily);
    }

    /**
     * 内部记录类，用于封装生成分布式序列所需的上下文信息。
     *
     * @param key          Redis 中的 Key，用于存储当前业务的计数器。
     * @param shouldExpire 标记该计数器是否需要设置过期时间（仅当 isDaily 为 true 时需要）。
     */
    private record IdContext(String key, boolean shouldExpire) {}

    /**
     * 核心方法：生成下一个分布式唯一 ID。
     * <p>
     * 逻辑流程：
     * 1. 构建基于日期和业务前缀的 Redis Key。
     * 2. 调用 Redis 原子自增获取序列号。
     * 3. 如果是按天模式且为当天的第一个序列，设置过期时间为当天结束。
     * 4. 将前缀、日期（可选）和格式化后的序列号拼接成最终结果。
     * </p>
     *
     * @param prefix  ID 前缀。
     * @param digit   序列号位数。
     * @param isDaily 是否按天重置。
     * @return 格式化后的唯一 ID。
     */
    public String nextId(String prefix, int digit, boolean isDaily) {
        // 获取当前日期字符串
        String dateStr = LocalDateTime.now().format(DATE_FORMATTER);

        // 1. 构建上下文：决定 Redis Key 的结构以及是否需要过期策略
        var ctx = isDaily
                ? new IdContext("%s%s:%s".formatted(BASE_PREFIX, prefix, dateStr), true)
                : new IdContext("%s%s:global".formatted(BASE_PREFIX, prefix), false);

        // 2. 获取自增序列号
        long sequence = getSequence(ctx);

        // 3. 格式化输出：构建指定位数的数字字符串（例如 %05d）
        String suffix = "%0" + digit + "d";
        return isDaily
                ? "%s%s%s".formatted(prefix, dateStr, suffix.formatted(sequence))
                : "%s%s".formatted(prefix, suffix.formatted(sequence));
    }

    /**
     * 从 Redis 获取并递增序列号，同时处理按天模式的过期设置。
     *
     * @param ctx 上下文对象，包含 Redis Key 和过期标志。
     * @return 递增后的序列号。
     */
    private long getSequence(IdContext ctx) {
        // 获取 Redis 原子长整型对象
        RAtomicLong atomicLong = redissonClient.getAtomicLong(ctx.key());
        // 执行原子自增操作
        long sequence = atomicLong.incrementAndGet();

        // 4. 使用 Instant 进行精确过期设置
        // 仅在序列号为 1（即当天的第一个请求）且需要过期时设置过期时间
        if (sequence == 1 && ctx.shouldExpire()) {
            // 计算当天 23:59:59 的 Instant 时间点
            var endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX)
                    .atZone(ZoneId.systemDefault())
                    .toInstant();
            // 设置过期时间，确保每天的数据自动清理
            atomicLong.expire(endOfDay);
        }
        return sequence;
    }
}
