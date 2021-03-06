package com.cqx.common.utils.list;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 本质是一个ArrayList，支持&lt;K, V&gt;
 *
 * @author chenqixu
 */
public class KVList<K, V> implements Cloneable, Serializable, IKVList<K, V> {
    private transient List<IKVList.Entry<K, V>> entrySet = new ArrayList<>();

    /**
     * 写入数据
     *
     * @param k
     * @param v
     */
    @Override
    public void put(K k, V v) {
        entrySet.add(new Entry<>(k, v));
    }

    /**
     * 按序号获取数据
     *
     * @param index
     * @return
     */
    @Override
    public IKVList.Entry<K, V> get(int index) {
        return entrySet.get(index);
    }

    /**
     * 支持循环
     *
     * @return
     */
    @Override
    public List<IKVList.Entry<K, V>> entrySet() {
        List<IKVList.Entry<K, V>> es;
        return (es = entrySet) == null ? (entrySet = new ArrayList<>()) : es;
    }

    /**
     * 大小
     *
     * @return
     */
    @Override
    public int size() {
        return entrySet.size();
    }

    /**
     * 按序号移除数据
     *
     * @param index
     */
    @Override
    public void remove(int index) {
        entrySet.remove(index);
    }

    /**
     * 按对象移除数据
     *
     * @param entry
     */
    @Override
    public void remove(IKVList.Entry entry) {
        entrySet.remove(entry);
    }

    /**
     * 返回key的列表
     *
     * @return
     */
    @Override
    public List<K> keys() {
        List<K> keyList = new ArrayList<>();
        for (IKVList.Entry<K, V> entry : entrySet()) {
            keyList.add(entry.getKey());
        }
        return keyList;
    }

    /**
     * 返回value的列表
     *
     * @return
     */
    @Override
    public List<V> values() {
        List<V> valueList = new ArrayList<>();
        for (IKVList.Entry<K, V> entry : entrySet()) {
            valueList.add(entry.getValue());
        }
        return valueList;
    }

    /**
     * 内部对象
     */
    static class Entry<K, V> implements IKVList.Entry<K, V> {
        private K k;
        private V v;

        Entry(K k, V v) {
            this.k = k;
            this.v = v;
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            this.v = value;
            return value;
        }

        @Override
        public boolean equals(Object o) {
            if (o != null) {
                Entry newEntry = (Entry) o;
                if (newEntry.getKey().equals(this.getKey())
                        && newEntry.getValue().equals(this.getValue()))
                    return true;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return (this.getKey().toString() + this.getValue().toString()).hashCode();
        }

        @Override
        public String toString() {
            return "[key] : " + this.getKey() + " , [value] : " + this.getValue();
        }
    }
}
