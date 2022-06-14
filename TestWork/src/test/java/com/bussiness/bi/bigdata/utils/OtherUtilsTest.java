package com.bussiness.bi.bigdata.utils;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;


import com.bussiness.bi.bigdata.utils.system.IRedo;
import com.bussiness.bi.bigdata.utils.system.RedoFactory;
import org.junit.Test;

public class OtherUtilsTest {

    private static MyLogger logger = MyLoggerFactory.getLogger(OtherUtilsTest.class);

    public void redo(int seq, String msg) {
        if (msg.equals("no") && seq > 0) {
            logger.info("seq：{}，msg：{}", seq, msg);
            redo(seq - 1, msg);
        }
    }

    public void redo(String msg) {
        logger.info("msg：{}", msg);
    }

    @Test
    public void redoTest() {
//        redo(3, "no");
        RedoFactory redoFactory = new RedoFactory();
        redoFactory.setMaxAttempts(3);
        redoFactory.exec(new IRedo() {
            @Override
            public void onRetry() {
                redo("no");
            }

            @Override
            public void retryCondition() {

            }

            @Override
            public void retryExceptionCondition() {

            }
        });
    }

    @Test
    public void getRunTime() {
        OtherUtils.addTimeTag(this);
        SleepUtils.sleepSecond(2);
        logger.info("OtherUtils.getTimeOut：{}", OtherUtils.getTimeOut(this));
        int i = 0;
        while (i < 10) {
            logger.info("OtherUtils.getTimeOut：{}", OtherUtils.getTimeOut(this));
            i++;
            SleepUtils.sleepMilliSecond(500);
        }
    }
}