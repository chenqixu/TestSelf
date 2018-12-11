package com.newland.bi.bigdata.time;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author chenqixu
 */
public class TimeHelper {

    protected static final Logger log = LoggerFactory.getLogger(TimeHelper.class);
    protected static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHH");

    /**
     * 时间比较
     *
     * <pre>
     * 	time1小于time2 -1
     * 	time1等于time2 0
     * 	time1大于time2 1
     * </pre>
     *
     * @param time1
     * @param time2
     * @return 文件周期
     */
    public static int timeComparison(String time1, String time2) {
        try {
            // SimpleDateFormat高并发情况下必须同步
            synchronized (simpleDateFormat) {
                long l1 = simpleDateFormat.parse(time1).getTime();
                long l2 = simpleDateFormat.parse(time2).getTime();
                return (l1 - l2 > 0) ? 1 : (l1 - l2 < 0 ? -1 : 0);
            }
        } catch (ParseException e) {
            log.error("★★★ 时间比较异常，时间1 {}，时间2 {}", time1, time2);
            log.error("★★★ 具体错误信息：{}", e.getMessage(), e);
            // e.printStackTrace();
        }
        return 2;
    }

    /**
     * time2减time1，时间差，单位秒
     *
     * @param time1  时间1
     * @param time2  时间2
     * @param format 格式化
     * @return
     */
    public static long timeSubtract(String time1, String time2, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            return (simpleDateFormat.parse(time2).getTime() - simpleDateFormat.parse(time1).getTime()) / 1000;
        } catch (ParseException e) {
            log.error(e.getMessage(), e);
        }
        return 0;
    }

    /**
     * 获取当前时间，格式yyyyMMddHHmmss
     *
     * @return
     */
    public static String getNow() {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(now);
    }
}
