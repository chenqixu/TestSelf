package com.cqx.common.utils.file;

import com.cqx.common.utils.log.MyLogger;
import com.cqx.common.utils.log.MyLoggerFactory;
import org.junit.Test;

public class PropertyUtilTest {

    private static MyLogger logger = MyLoggerFactory.getLogger(PropertyUtilTest.class);

    @Test
    public void getProperty() {
        logger.info("test：{}", 1);
        logger.warn("test：{}", 2);
        logger.debug("test：{}", 3);
        logger.error("test");
    }
}