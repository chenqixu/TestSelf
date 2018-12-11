package com.newland.bi.bigdata.utils;

import com.cqx.process.LogInfoFactory;
import com.cqx.process.Logger;
import com.newland.bi.bigdata.utils.system.IRedo;
import com.newland.bi.bigdata.utils.system.RedoFactory;
import org.junit.Test;

public class OtherUtilsTest {

    private static Logger logger = LogInfoFactory.getInstance(OtherUtilsTest.class);

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
}