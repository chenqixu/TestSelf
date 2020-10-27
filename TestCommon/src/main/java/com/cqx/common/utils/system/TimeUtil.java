package com.cqx.common.utils.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 *
 * @author chenqixu
 */
public class TimeUtil {

    private static final Logger logger = LoggerFactory.getLogger(TimeUtil.class);
    private static SimpleDateFormat ymdhmsFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 补0
     *
     * @param date
     * @param len
     * @return
     */
    public static String supplementZero(String date, int len) {
        StringBuffer sb = null;
        if (date != null && date.trim().length() > 0 && !date.contains(":")) {
            sb = new StringBuffer(date);
            int datalen = date.length();
            if (datalen < len) {
                for (int i = datalen; i < len; i++) {
                    sb.append("0");
                }
            }
        }
        return sb != null ? sb.toString() : null;
    }

    public static String supplementZero(String date) {
        return supplementZero(date, 14);
    }

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
     * @param format
     * @return 文件周期
     */
    public static int timeComparison(String time1, String time2, String format) {
        if (time1 != null && time2 != null) {
            int len = format.length();
            // 先补0，再比较
            String _time1 = supplementZero(time1, len);
            String _time2 = supplementZero(time2, len);
            // 如果SimpleDateFormat是全局变量，高并发情况下必须同步
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            try {
                long l1 = simpleDateFormat.parse(_time1).getTime();
                long l2 = simpleDateFormat.parse(_time2).getTime();
                return (l1 - l2 > 0) ? 1 : (l1 - l2 < 0 ? -1 : 0);
            } catch (ParseException e) {
                logger.error("★★★ 时间比较异常，时间1 {}，时间2 {}", time1, time2);
                logger.error("★★★ 具体错误信息：" + e.getMessage(), e);
            }
        }
        return 2;
    }

    public static int timeComparison(String time1, String time2) {
        return timeComparison(time1, time2, "yyyyMMddHH");
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
            logger.error(e.getMessage(), e);
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

    /**
     * 获取当前时间，格式yyyyMMddHHmmss
     *
     * @return
     */
    public static String getNow(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(now);
    }

    /**
     * 时间戳转时间
     *
     * @param value
     */
    public static void timestampToDate(long value) {
        Date date = new Date(value);
        logger.info("timestampToDate：{}", ymdhmsFormat.format(date));
    }

    public static String getDateFormat(String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(new Date());
    }

    /**
     * 时间偏移操作
     *
     * @param type
     * @param devtiation
     * @param format
     * @return
     */
    public static String devtiation(int type, int devtiation, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(type, -1 * devtiation);
        Date add = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        return simpleDateFormat.format(add);
    }
}
