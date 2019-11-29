package com.newland.bi.bigdata.map;

import java.util.HashMap;
import java.util.Map;

/**
 * MapUtil
 *
 * @author chenqixu
 */
public class MapUtil {

    /**
     * 以map2为准，合并到map1中
     *
     * @param map1
     * @param map2
     * @param <K>
     * @param <V>
     * @return
     */
    public <K, V> Map<K, V> mergeMap(Map<K, V> map1, Map<K, V> map2) {
        if (map1 != null && map2 != null) {
            Map<K, V> newMap = new HashMap<>();
            newMap.putAll(map1);
            newMap.putAll(map2);
            return newMap;
        } else {
            throw new NullPointerException("map1或map2值为空，map1：" + map1 + "，map2：" + map2);
        }
    }

}
