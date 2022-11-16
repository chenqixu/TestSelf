package com.cqx.common.utils.localcache.mapdb;

import com.cqx.common.utils.system.SleepUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

public class MapDBUtilTest {

    @Test
    public void put() throws IOException {
        String fileName = "d:\\tmp\\data\\mapdb\\test1";
        try (MapDBUtil mapDBUtil = new MapDBUtil(fileName)) {
            mapDBUtil.put("a", "1234");
            mapDBUtil.put("b", "5678");
            SleepUtil.sleepSecond(10);
        }
    }

    @Test
    public void get() throws IOException {
        String fileName = "d:\\tmp\\data\\mapdb\\test1";
        try (MapDBUtil mapDBUtil = new MapDBUtil(fileName)) {
            for (Map.Entry<String, String> entry : mapDBUtil.getMap("map").entrySet()) {
                System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
            }
        }
    }
}