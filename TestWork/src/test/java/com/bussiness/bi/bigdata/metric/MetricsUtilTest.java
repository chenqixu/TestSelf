package com.bussiness.bi.bigdata.metric;

import com.bussiness.bi.bigdata.utils.SleepUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsUtilTest {

    private static Logger logger = LoggerFactory.getLogger(MetricsUtilTest.class);

    @Test
    public void metricTest() {
        String taskName = "test";
        MetricsUtil metricTool = MetricsUtil.builder();
//        MetricTool.addTimeTag(taskName);
        metricTool.addTimeTag();
        SleepUtils.sleepMilliSecond(1500);
//        MetricTool.start(taskName);
//        logger.info("time pass：{}", MetricTool.getTimeOut(taskName));
        logger.info("time pass：{}", metricTool.getTimeOut());
        metricTool.addTimeTag();
        SleepUtils.sleepMilliSecond(1500);
        logger.info("time pass：{}", metricTool.getTimeOut());
    }
}