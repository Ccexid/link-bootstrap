package me.link.bootstrap.shared.kernel.component;

import org.redisson.api.RBloomFilter;

/**
 * Redis/Redisson backed Bloom filter access contract.
 */
public interface BloomFilterStore<T> {

    /**
     * 获取BloomFilter。
     */
    RBloomFilter<T> getBloomFilter();

    /**
     * 处理缺失。
     */
    default boolean missing(T value) {
        RBloomFilter<T> bloomFilter = getBloomFilter();
        return bloomFilter == null || !bloomFilter.contains(value);
    }
}
