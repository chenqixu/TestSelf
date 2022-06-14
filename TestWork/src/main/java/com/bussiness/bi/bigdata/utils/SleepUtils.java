package com.bussiness.bi.bigdata.utils;

import java.util.concurrent.TimeUnit;

/**
 * 压抑异常的sleep
 *
 * @author chenqixu
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

    class TimerTool {

    }

}
