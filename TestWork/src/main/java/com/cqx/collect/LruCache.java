package com.cqx.collect;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LruCache
 *
 * @author chenqixu
 */
public class LruCache<K, V> extends LinkedHashMap<K, V> {
    private final int maxSize;

    public LruCache(int maxSize) {
        this.maxSize = maxSize;
    }

    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return this.size() > this.maxSize;
    }
}
