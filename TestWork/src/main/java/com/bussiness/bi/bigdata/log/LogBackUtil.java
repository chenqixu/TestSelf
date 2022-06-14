package com.bussiness.bi.bigdata.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;

/**
 * LogBackUtil
 *
 * @author chenqixu
 */
public class LogBackUtil {

    /**
     * 手工初始化logback配置文件，不知道为什么要手工初始化
     *
     * @param file logback.xml
     * @throws JoranException
     */
    public static void init(String file) throws JoranException {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator jc = new JoranConfigurator();
        jc.setContext(loggerContext);
        loggerContext.reset();
        jc.doConfigure(file);
    }
}
