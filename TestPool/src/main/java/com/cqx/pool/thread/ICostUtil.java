package com.cqx.pool.thread;

/**
 * ICostUtil
 *
 * @author chenqixu
 */
public interface ICostUtil {
    void start();

    void end();

    boolean tag(long limitTime);

    long getCost();
}
