package com.cqx.common.utils.list;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KVListTest {
    private static final Logger logger = LoggerFactory.getLogger(KVListTest.class);

    @Test
    public void put() {
        KVList<String, String> kvList = new KVList<>();
        kvList.put("1", "2");
        kvList.put("1", "3");
        logger.info("{}", kvList.size());
        for (IKVList.Entry<String, String> entry : kvList.entrySet()) {
            logger.info("entry：{}", entry);
        }
        logger.info("{}", kvList.get(0));
    }
}