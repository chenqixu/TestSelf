package com.cqx.common.utils.file;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RAFFileMangerCenterTest {
    private static final Logger logger = LoggerFactory.getLogger(RAFFileMangerCenterTest.class);
    private RAFFileMangerCenter rafFileMangerCenter;

    @Before
    public void setUp() throws Exception {
        rafFileMangerCenter = new RAFFileMangerCenter("d:\\tmp\\data\\raffile\\sm", 50);
    }

    @After
    public void tearDown() throws Exception {
        if (rafFileMangerCenter != null) {
            rafFileMangerCenter.close();
            logger.info("删除文件：{}，删除结果：{}", rafFileMangerCenter.getFile_name(), rafFileMangerCenter.del());
        }
    }

    @Test
    public void write() {
        for (int i = 0; i < 10; i++) {
            int ret = rafFileMangerCenter.write(i + "");
            logger.info("写入结果：{}", ret);
        }
    }

    @Test
    public void read() {
        String msg;
        while ((msg = rafFileMangerCenter.read()) != null) {
            logger.info("msg：{}", msg);
        }
    }
}