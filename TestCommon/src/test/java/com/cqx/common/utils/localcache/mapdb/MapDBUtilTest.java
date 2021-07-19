package com.cqx.common.utils.localcache.mapdb;

import org.junit.Test;

import java.io.IOException;

public class MapDBUtilTest {

    @Test
    public void put() throws IOException {
        String fileName = "d:\\tmp\\data\\mapdb\\test1";
        try (MapDBUtil mapDBUtil = new MapDBUtil(fileName)) {
            mapDBUtil.put("a", "1234");
        }
    }

    @Test
    public void get() throws IOException {
        String fileName = "d:\\tmp\\data\\mapdb\\test1";
        try (MapDBUtil mapDBUtil = new MapDBUtil(fileName)) {
            System.out.println(mapDBUtil.get("a"));
        }
    }
}