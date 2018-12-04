package com.cqx.process;

/**
 * 日志接口
 *
 * @author chenqixu
 */
public interface Logger {
    void error(String msg);

    void error(String msg, Throwable throwable);

    void info(String msg);

    void info(String msg, Object... objs);

    void warn(String msg);

    void warn(String msg, Object... objs);

    void debug(String msg);

    void debug(String msg, Object... objs);

    void debug(String msg, Throwable throwable);
}
