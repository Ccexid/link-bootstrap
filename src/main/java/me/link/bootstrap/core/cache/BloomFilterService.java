package me.link.bootstrap.core.cache;

import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;

/**
 * 布隆过滤器服务类
 * 用于高效判断热点数据（如 ID）是否存在，减少缓存穿透风险
 */
@Service
public class BloomFilterService {

    // Redisson 客户端实例，用于操作 Redis 中的布隆过滤器
    private final RedissonClient redissonClient;
    
    // 热点数据布隆过滤器实例
    private RBloomFilter<String> hotDataFilter;

    /**
     * 构造函数注入 RedissonClient
     * @param redissonClient Redisson 客户端实例
     */
    public BloomFilterService(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    /**
     * 初始化方法，在 Bean 创建后自动执行
     * 作用：获取并初始化名为 "bloom:hot-data" 的布隆过滤器
     */
    @PostConstruct
    public void init() {
        // 从 Redis 中获取名为 "bloom:hot-data" 的布隆过滤器实例
        // 该名称作为键存储在 Redis 中，用于标识此过滤器
        hotDataFilter = redissonClient.getBloomFilter("bloom:hot-data");
        
        // 初始化布隆过滤器参数：
        // 第一个参数 (1000000L): 预期插入的元素数量（100 万条数据）
        // 第二个参数 (0.03): 允许的误判率（3%），即判断存在但实际不存在的概率上限
        // tryInit 方法仅在过滤器未初始化时执行初始化，避免重复初始化覆盖已有数据
        hotDataFilter.tryInit(1000000L, 0.03);
    }

    /**
     * 向布隆过滤器中添加元素
     * @param id 要添加的数据 ID
     * 注意：布隆过滤器不支持删除操作
     */
    public void add(String id) {
        // 将指定 ID 添加到布隆过滤器中
        // 返回值为布尔类型，但通常忽略，因为添加操作总是成功（除非内存不足）
        hotDataFilter.add(id);
    }

    /**
     * 判断元素是否可能存在
     * @param id 要查询的数据 ID
     * @return true 表示元素可能存在（有少量误判可能），false 表示元素一定不存在
     */
    public boolean mayExist(String id) {
        // 检查指定 ID 是否存在于布隆过滤器中
        // 返回 true 时：元素可能存在（存在误判概率）
        // 返回 false 时：元素绝对不存在（无误判）
        return hotDataFilter.contains(id);
    }
}