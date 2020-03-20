package com.cqx.util;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Test;

public class HttpUtilTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(HttpUtilTest.class);

    @Test
    public void doSend() {
        HttpUtil httpUtil = new HttpUtil();
        String result = httpUtil.doGet("http://www.baidu.com/");
        logger.info("result：{}", result);
    }
}