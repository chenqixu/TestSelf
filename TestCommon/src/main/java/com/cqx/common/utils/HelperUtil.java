package com.cqx.common.utils;

import com.cqx.common.utils.system.SleepUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HelperUtil
 *
 * @author chenqixu
 */
public class HelperUtil {
    private static final Logger logger = LoggerFactory.getLogger(HelperUtil.class);

    public static void main(String[] args) {
        logger.info("args.size : {}.", args.length);
        logger.info("this is helper util.");
        for (int i = 0; i < 20; i++) {
            SleepUtil.sleepMilliSecond(500);
            logger.info("i : {}", i);
        }
        SleepUtil.sleepMilliSecond(500);
        logger.info("stop.");
    }
}
