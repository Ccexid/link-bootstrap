package me.link.bootstrap.shared.kernel.component;

import org.redisson.api.RBloomFilter;

/**
 * Redis/Redisson backed Bloom filter access contract.
 */
public interface BloomFilterStore<T> {

    RBloomFilter<T> getBloomFilter();

    default boolean missing(T value) {
        RBloomFilter<T> bloomFilter = getBloomFilter();
        return bloomFilter == null || !bloomFilter.contains(value);
    }
}
