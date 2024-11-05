package com.cqx.logs;

import org.apache.log4j.Logger;

/**
 * 测试Logger是否会输出堆栈日志
 *
 * @author chenqixu
 */
public class LogsTest {
    private static final Logger logger = Logger.getLogger(LogsTest.class);

    public static void main(String[] args) {
        logger.info("info test.");
        logger.warn("warn test.");
        logger.error("error test.", new RuntimeException("runtimeEx."));
    }
}
