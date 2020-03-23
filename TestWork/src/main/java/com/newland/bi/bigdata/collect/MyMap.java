package com.newland.bi.bigdata.collect;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * MyMap
 *
 * @author chenqixu
 */
public class MyMap<K, V> implements Map<K, V> {

    private static final MyLogger logger = MyLoggerFactory.getLogger(MyMap.class);
    /**
     * 输出一个int的二进制数
     *
     * @param num
     */
    public static void printInfo(int num) {
        logger.info(Integer.toBinaryString(num));
    }

    public static void print(String msg) {
        logger.info(msg + "：");
    }

    /**
     * 大小
     *
     * @return
     */
    @Override
    public int size() {
        return 0;
    }

    /**
     * 是否为空
     *
     * @return
     */
    @Override
    public boolean isEmpty() {
        return false;
    }

    /**
     * 查找是否有这个key
     *
     * @param key
     * @return
     */
    @Override
    public boolean containsKey(Object key) {
        return false;
    }

    /**
     * 查找是否有这个value
     *
     * @param value
     * @return
     */
    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public V get(Object key) {
        return null;
    }

    @Override
    public V put(K key, V value) {
        return null;
    }

    @Override
    public V remove(Object key) {
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {

    }

    @Override
    public void clear() {

    }

    @Override
    public Set<K> keySet() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return null;
    }
}
