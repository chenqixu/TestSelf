package com.cqx.common.utils.localcache.lmdb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LmdbUtilTest {
    private static final Logger logger = LoggerFactory.getLogger(LmdbUtilTest.class);
    private LmdbUtil lmdbUtil;

    @Before
    public void setUp() throws Exception {
        lmdbUtil = new LmdbUtil("d:\\tmp\\data\\lmdb\\Test1\\a", true);
    }

    @After
    public void tearDown() throws Exception {
        if (lmdbUtil != null) lmdbUtil.release();
    }

    @Test
    public void putValueToDb() {
        String key = "123";
        lmdbUtil.put(key, "abc");
        String val = lmdbUtil.get(key);
//        lmdbUtil.putValueToDb(key, "abc");
//        String val = lmdbUtil.getValueByKey(key);
        logger.info("key：{}，val：{}", key, val);
        logger.info("db_name：{}", lmdbUtil.getDbName());
    }
}