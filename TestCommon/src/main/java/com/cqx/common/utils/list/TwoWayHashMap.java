package com.cqx.common.utils.list;

import java.util.*;

/**
 * 双向HashMap<br>
 * &nbsp;&nbsp;正向的key不可重复，反向则允许，所以反向的value是一个集合
 *
 * @author chenqixu
 */
public class TwoWayHashMap<K, V> {
    private Map<K, V> forward;
    private Map<V, List<K>> reverse;

    public TwoWayHashMap() {
        forward = new HashMap<>();
        reverse = new HashMap<>();
    }

    public V put(K key, V value) {
        V v = forward.put(key, value);
        List<K> keyList = reverse.get(value);
        if (keyList == null) {
            keyList = new ArrayList<>();
            reverse.put(value, keyList);
        }
        keyList.add(key);
        return v;
    }

    public V get(Object key) {
        return forward.get(key);
    }

    public List<K> keysGet(Object key) {
        return reverse.get(key);
    }

    public V remove(Object key) {
        V v = forward.remove(key);
        List<K> keyList = reverse.get(v);
        for (Iterator<K> it = keyList.iterator(); it.hasNext(); ) {
            if (it.next().equals(key)) {
                it.remove();
                break;
            }
        }
        return v;
    }

    public void keysRemove(Object key) {
        List<K> keyList = reverse.get(key);
        for (Iterator<K> it = keyList.iterator(); it.hasNext(); ) {
            forward.remove(it.next());
        }
        reverse.remove(key);
    }

    public void clear() {
        forward.clear();
        reverse.clear();
    }

    public String toString() {
        return forward.toString();
    }
}
