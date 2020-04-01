package com.newland.bi.bigdata.collect;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import com.newland.aig2.MD5;

import java.util.*;

/**
 * MyMap
 *
 * @author chenqixu
 */
public class MyMap<K, V> extends HashMap<K, V> {

    private static final MyLogger logger = MyLoggerFactory.getLogger(MyMap.class);
    private int mod = 0;
    private int myMapDataCnt = 0;
    private Map<Long, MyMapData> myMapData = new HashMap<>();

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

    public void testHash(String key) {
        int h = key.hashCode();
        h ^= (h >>> 20) ^ (h >>> 12);
        int hash = h ^ (h >>> 7) ^ (h >>> 4);
        logger.info("hash：{}", hash);
    }

    public void Mod() {
        //随机50个不重复的数，对50取模
        Random random = new Random();
        Map<Long, MyMapData> map = new HashMap<>();
//        long[] sources = {13509323820L, 13509323821L, 13509323822L, 13509323823L, 13509323824L, 13509323825L};
//        int mod_length =sources.length;
        int mod_length = 50;
        for (int i = 0; i < mod_length; i++) {
            long source = random.nextInt(1000);
            while (map.get(source) != null) {
                source = random.nextInt(1000);
            }
//            long source = sources[i];
            long dst = source % mod_length;
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

    public void setMod(int mod) {
        this.mod = mod;
    }

    public void add(Long value) {
        myMapDataCnt++;
//        long key = Math.abs(value.hashCode()) % mod;
//        long key = Math.abs(value.hashCode());
        long key = Long.valueOf(MD5.toMD5(String.valueOf(value)));
        MyMapData data = myMapData.get(key);
        if (data != null) {
            data.add(value);
        } else {
            myMapData.put(key, new MyMapData(value));
        }
    }

    public void printMyMapData() {
        logger.info("myMapData.size：{}，myMapDataCnt：{}", myMapData.size(), myMapDataCnt);
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
