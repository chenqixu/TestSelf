package com.newland.bi.bigdata.metric;

import com.newland.bi.bigdata.utils.SleepUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricToolTest {

    private static Logger logger = LoggerFactory.getLogger(MetricToolTest.class);

    @Test
    public void metricTest() {
        String taskName = "test";
        MetricTool.start(taskName);
        SleepUtils.sleepMilliSecond(1500);
        MetricTool.start(taskName);
        logger.info("time pass：{}", MetricTool.end(taskName));
    }
}