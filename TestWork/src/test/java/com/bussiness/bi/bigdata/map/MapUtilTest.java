package com.bussiness.bi.bigdata.map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class MapUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(MapUtilTest.class);
    private MapUtil mapUtil = new MapUtil();

    @Test
    public void mergeMap() {
        Map<String, String> map1 = new HashMap<>();
        map1.put("field1", "aa");
        map1.put("field2", "bb");
        Map<String, String> map2 = new HashMap<>();
        map2.put("field2", "c");
        logger.info("mergeMap：{}，map1：{}，map2：{}", mapUtil.mergeMap(map1, map2), map1, map2);
    }
}