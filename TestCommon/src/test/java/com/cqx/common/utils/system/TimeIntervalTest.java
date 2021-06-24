package com.cqx.common.utils.system;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeIntervalTest {
    private static final Logger logger = LoggerFactory.getLogger(TimeIntervalTest.class);

    @Test
    public void isTimeOut() {
        TimeInterval timeInterval = new TimeInterval(5000L);
        for (int i = 0; i < 20; i++) {
            boolean isTimeOut = timeInterval.isTimeOut();
            logger.info("isTimeOut：{}", isTimeOut);
            if (isTimeOut) timeInterval.newLastDealTime();
            SleepUtil.sleepSecond(1);
        }
    }
}