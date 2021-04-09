package com.cqx.common.utils.list;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TwoWayHashMapTest {
    private static final Logger logger = LoggerFactory.getLogger(TwoWayHashMapTest.class);
    private TwoWayHashMap<Integer, String> twoWayHashMap = new TwoWayHashMap();

    @Test
    public void put() {
        for (int i = 1; i < 10; i++) twoWayHashMap.put(i, "a");
        twoWayHashMap.put(11, "b");
        logger.info("{} {} {}", twoWayHashMap, twoWayHashMap.get(2), twoWayHashMap.keysGet("a"));
        twoWayHashMap.remove(2);
        logger.info("{} {} {}", twoWayHashMap, twoWayHashMap.get(2), twoWayHashMap.keysGet("a"));
        twoWayHashMap.keysRemove("a");
        logger.info("{} {}", twoWayHashMap, twoWayHashMap.keysGet("a"));
    }

    @Test
    public void get() {
    }

    @Test
    public void reverseGet() {
    }

    @Test
    public void remove() {
    }

    @Test
    public void clear() {
    }
}