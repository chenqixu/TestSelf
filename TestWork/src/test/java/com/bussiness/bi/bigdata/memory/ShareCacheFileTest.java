package com.bussiness.bi.bigdata.memory;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ShareCacheFileTest {

    private static final MyLogger logger = MyLoggerFactory.getLogger(ShareCacheFileTest.class);
    private ShareCacheFile sm;
    private String str = "中文测试123";
    private String str1 = "应该可能";

    @Before
    public void setUp() {
        sm = new ShareCacheFile("d:\\tmp\\data\\mccdr\\", "test", 1);
    }

    @After
    public void setDown() {
        sm.closeSMFile();
    }

    @Test
    public void write() throws Exception {
        sm.write(0, str.getBytes().length, str.getBytes("UTF-8"));
        sm.write(0, str1.getBytes().length, str1.getBytes("UTF-8"));
    }

    @Test
    public void read() throws Exception {
        byte[] b = new byte[str.getBytes().length];
        sm.read(0, str.getBytes().length, b);
        logger.info("msg：{}", new String(b, "UTF-8"));
    }
}