package com.cqx.common.utils.compress;

import com.cqx.common.utils.system.TimeCostUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GZUtilTest {

    private static final Logger logger = LoggerFactory.getLogger(GZUtilTest.class);

    @Test
    public void write() throws IOException {
        TimeCostUtil timeCostUtil = new TimeCostUtil();
        timeCostUtil.start();
        int max_size = 100 * 1024 * 1024;
        String fileName = "d:\\tmp\\data\\jk\\jk2.data.gz";
        GZUtil gzUtil;
        gzUtil = GZUtil.buildMemory(true);
//        gzUtil = GZUtil.buildFile(fileName, true);
        for (int i = 0; i < 10000; i++) {
            gzUtil.write("您好，北京欢迎您！我们一起相约在2008北京奥运会！".getBytes());
            if (i % 1000 == 0) {
                logger.info("before {}", gzUtil.size());
                gzUtil.flush();
                logger.info("after {}", gzUtil.size());
            }
        }
        gzUtil.flush();
        logger.info("flush size {}", gzUtil.size());
        gzUtil.close();
        timeCostUtil.stop();
        logger.info("cost {}，close size {}", timeCostUtil.getCost(), gzUtil.size());
//        gzUtil.saveMemoryToFile(fileName);
    }
}