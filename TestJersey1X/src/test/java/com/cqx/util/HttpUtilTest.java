package com.cqx.util;

import com.cqx.common.utils.log.LogUtil;
import org.junit.Test;

public class HttpUtilTest {

    private static final LogUtil logger = LogUtil.getLogger(HttpUtilTest.class);

    @Test
    public void doSend() {
        HttpUtil httpUtil = new HttpUtil();
        String result = httpUtil.doGet("http://www.baidu.com/");
        logger.info("result：{}", result);
    }
}