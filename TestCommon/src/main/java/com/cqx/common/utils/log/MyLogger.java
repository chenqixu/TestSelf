package com.cqx.common.utils.log;

/**
 * 日志接口
 *
 * @author chenqixu
 */
public interface MyLogger {
    void error(Object msg);

    void error(String msg);

    void error(String msg, Throwable throwable);

    void warn(Object msg);

    void warn(String msg);

    void warn(String msg, Object... param);

    void info(Object msg);

    void info(String msg);

    void info(String msg, Object... param);

    void debug(Object msg);

    void debug(String msg);

    void debug(String msg, Object... param);
}
