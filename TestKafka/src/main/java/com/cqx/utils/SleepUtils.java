package com.cqx.utils;

import java.util.concurrent.TimeUnit;

/**
 * 压抑异常的sleep
 *
 * @author huangxw
 * @date 2018-10-23
 */
public class SleepUtils {

    /**
     * 压抑异常的sleep
     *
     * @param timeout 几秒
     */
    public static void sleepSecond(long timeout) {
        try {
            TimeUnit.SECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }

    /**
     * 压抑异常的sleep
     *
     * @param timeout 几豪秒
     */
    public static void sleepMilliSecond(long timeout) {
        try {
            TimeUnit.MILLISECONDS.sleep(timeout);
        } catch (InterruptedException e) {
        }
    }
}
