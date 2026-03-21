package me.link.bootstrap.core.utils;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import java.time.Duration;

/**
 * 基于 Redisson 的缓存工具类
 * 提供静态方法以便在非 Spring 管理的类中方便地访问缓存
 */
@Component
public class CacheUtils {

    // Redisson 客户端实例，用于操作 Redis
    private final RedissonClient redissonClient;
    
    // 静态实例引用，用于在静态方法中访问非静态的 redissonClient
    private static CacheUtils instance;

    /**
     * 构造函数，由 Spring 容器注入 RedissonClient
     * 同时将当前实例赋值给静态变量，供静态方法使用
     * @param redissonClient Spring 注入的 Redisson 客户端
     */
    public CacheUtils(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
        instance = this;
    }

    /**
     * 从缓存中获取指定键的值
     * 使用泛型方法以支持更好的类型推断 (JDK 17+)
     * 
     * @param key 缓存键
     * @param <T> 期望返回值的类型
     * @return 缓存中的值，如果键不存在则返回 null
     */
    public static <T> T get(String key) {
        // 获取对应键的桶对象，桶是 Redisson 中操作单个对象的基本单元
        RBucket<T> bucket = instance.redissonClient.getBucket(key);
        // 从桶中读取并返回数据
        return bucket.get();
    }

    /**
     * 向缓存中设置指定键的值，并设定过期时间
     * 使用泛型方法以支持更好的类型推断 (JDK 17+)
     * 
     * @param key 缓存键
     * @param value 要存储的值
     * @param ttl 值的存活时间 (Time To Live)
     * @param <T> 值的类型
     */
    public static <T> void set(String key, T value, Duration ttl) {
        // 获取对应键的桶对象
        RBucket<T> bucket = instance.redissonClient.getBucket(key);
        // 将值写入桶中，并设置过期时间
        bucket.set(value, ttl);
    }
}