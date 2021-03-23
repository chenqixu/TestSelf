package com.cqx.common.utils.list;

import java.util.List;

/**
 * IKVList
 *
 * @author chenqixu
 */
public interface IKVList<K, V> {

    void put(K k, V v);

    Entry<K, V> get(int index);

    List<Entry<K, V>> entrySet();

    int size();

    void remove(int index);

    void remove(Entry entry);

    interface Entry<K, V> {
        K getKey();

        V getValue();

        V setValue(V value);

        boolean equals(Object o);

        int hashCode();
    }
}
