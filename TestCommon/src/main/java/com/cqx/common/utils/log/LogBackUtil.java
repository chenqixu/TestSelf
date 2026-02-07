package com.cqx.common.utils.log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.LoggerFactory;

import java.net.URL;

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

        StatusPrinter.print(loggerContext);
    }

    /**
     * 打印Logger配置
     */
    public static void printLoggerConfig() {
        org.slf4j.ILoggerFactory iLoggerFactory = null;
        try {
            System.out.println("==获取 SLF4J 的 LoggerContext==");
            // 获取 SLF4J 的 LoggerContext
            iLoggerFactory = LoggerFactory.getILoggerFactory();
            System.out.println("SLF4J ILoggerFactory 实现类: " + iLoggerFactory.getClass().getName());
        } catch (Exception e) {
            System.err.println("获取SLF4J ILoggerFactory 实现类出错！");
        }

        if (iLoggerFactory != null) {
            try {
                System.out.println("==获取LoggerContext实例==");
                // 获取LoggerContext实例
                LoggerContext loggerContext = (LoggerContext) iLoggerFactory;
                System.out.println("==打印Logback的内部状态==");
                // 打印Logback的内部状态
                StatusPrinter.print(loggerContext);
            } catch (Exception e) {
                System.err.println("获取LoggerContext实例，打印Logback的内部状态出错！");
            }
        }

        // 尝试获取 logback.xml
        URL base = Thread.currentThread().getContextClassLoader().getResource("");
        System.out.println("类路径=" + base.getPath());
        URL url = Thread.currentThread().getContextClassLoader().getResource("logback.xml");
        if (url == null) {
            System.out.println("在类路径中未找到 logback.xml 文件。");
        } else {
            System.out.println("成功找到 logback.xml，路径为: " + url.getPath());
        }
    }
}
