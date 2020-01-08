package com.newland.bi.bigdata.thread.local;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestContextHolderTest {

    private static final Logger logger = LoggerFactory.getLogger(RequestContextHolderTest.class);

    @Test
    public void getRequest() {
        logger.info("getRequest：{}", RequestContextHolder.getRequest());
    }

    @Test
    public void getOtherRequest() {
        RequestContextHolder.getOtherRequest();
    }
}