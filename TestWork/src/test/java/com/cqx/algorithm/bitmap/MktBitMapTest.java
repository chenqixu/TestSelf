package com.cqx.algorithm.bitmap;

import com.cqx.common.utils.jdbc.DBBean;
import com.cqx.common.utils.jdbc.DBType;
import com.newland.bi.bigdata.time.TimeCostUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class MktBitMapTest {
    private static final Logger logger = LoggerFactory.getLogger(MktBitMapTest.class);
    private MktBitMap mktBitMap;

    @Before
    public void setUp() throws Exception {
        mktBitMap = new MktBitMap();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void addMkt() {
        TimeCostUtil tc = new TimeCostUtil();
        tc.start();
        for (int i = 550000; i > 500000; i--) mktBitMap.addMkt(i);
        mktBitMap.sort();
        logger.info("cost：{}", tc.stopAndGet());
        long val = mktBitMap.getValueByIndex(100);
        long index = mktBitMap.getIndexByValue(val);
        logger.info("{} {}", val, index);
    }

    @Test
    public void test() {
        DBBean dbBean = new DBBean();
        dbBean.setDbType(DBType.POSTGRESQL);
        dbBean.setTns("jdbc:postgresql://10.1.8.206:5432/sentry");
        dbBean.setUser_name("sentry");
        dbBean.setPass_word("sentry");
        dbBean.setPool(false);
        try {
            mktBitMap.init(dbBean);
            mktBitMap.mktToIndex();
            mktBitMap.createUserMktList();
            mktBitMap.isRe(13805031186L, 590430254273L);
            mktBitMap.isRe(13906903023L, 590430252683L);
            mktBitMap.isRe(13905008690L, 599430282203L);
            mktBitMap.isRe(13905008690L, 511430282203L);
        } finally {
            mktBitMap.close();
        }
    }
}