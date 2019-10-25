package com.newland.bi.bigdata.utils;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class OtherUtils {
    private static final long TIME_OUT = 5000;//一小时过期
    private static Map<Object, TimeOut> timeMaps = new HashMap<>();

    public static OtherUtils newbuilder() {
        return new OtherUtils();
    }

    public static String getCurrentPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        String pid = name.split("@")[0];
        return pid;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 增加监控
     *
     * @param obj
     */
    public static void addTimeTag(Object obj) {
        timeMaps.put(obj, TimeOut.builder(obj));
        System.out.println("add：" + obj);
    }

    /**
     * 获取对象的运行时长
     *
     * @param obj
     * @return
     */
    public static long getTimeOut(Object obj) {
        return timeMaps.get(obj) == null ? 0l : timeMaps.get(obj).getTime();
    }

    /**
     * @return 返回微秒
     */
    public static Long getMicTime() {
        Long cutime = System.currentTimeMillis() * 1000; // 微秒
        Long nanoTime = System.nanoTime(); // 纳秒
        return cutime + (nanoTime - nanoTime / 1000000 * 1000000) / 1000;
    }

    /**
     * @return 返回毫秒
     */
    public static Long getMillisTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取调用者的类名
     *
     * @return
     */
    public static String getCallerName() {
        return new Exception().getStackTrace()[2].getClassName();
    }

    static class TimeOut {
        Object object;
        long time = 0l;
        Timer timer;

        public TimeOut(final Object object) {
            this.object = object;
            time = System.currentTimeMillis();
        }

        public TimeOut(final Object object, boolean isTimer) {
            this(object);
            if (isTimer) {
                timer = new Timer(true);
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        synchronized (timeMaps) {
                            if (timeMaps.get(object) != null) {
                                timeMaps.remove(object);
                                System.out.println("remove：" + object);
                            }
                        }
                    }
                }, TIME_OUT);
            }
        }

        public static TimeOut builder(Object object) {
            return new TimeOut(object);
        }

        /**
         * 返回运行时长之后从Map中移除，避免内存泄露
         *
         * @return
         */
        public long getTime() {
            synchronized (timeMaps) {
                timeMaps.remove(object);
            }
            return System.currentTimeMillis() - time;
        }
    }
}
