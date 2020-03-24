package com.newland.bi.bigdata.collect;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;

import java.util.*;

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

    public void Mod() {
        //随机50个不重复的数，对50取模
        Random random = new Random();
        Map<Long, MyMapData> map = new HashMap<>();
        long[] sources = {13509323820L, 13509323821L, 13509323822L, 13509323823L, 13509323824L, 13509323825L};
        for (int i = 0; i < sources.length; i++) {
//            int source = random.nextInt(1000);
//            while (map.get(source) != null) {
//                source = random.nextInt(1000);
//            }
            long source = sources[i];
            long dst = source % sources.length;
            MyMapData myMapData = map.get(dst);
            if (myMapData != null) {
                myMapData.add(source);
            } else {
                myMapData = new MyMapData(source);
            }
            map.put(dst, myMapData);
        }
        logger.info("map.size：{}，map：{}", map.size(), map);
    }

    class MyMapData {
        List<Long> dataList = new ArrayList<>();

        MyMapData(Long data) {
            add(data);
        }

        void add(long data) {
            dataList.add(data);
        }

        public String toString() {
            return dataList.toString();
        }
    }
}
