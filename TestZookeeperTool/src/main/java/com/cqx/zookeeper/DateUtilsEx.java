package com.cqx.zookeeper;

//import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * DateUtils的扩展类
 * */
public class DateUtilsEx {
    // 本地的TimeZone
    private static final TimeZone LOCAL_UTC_TIME_ZONE = TimeZone.getTimeZone("GMT+8");
    // 字符串转成时间类型时, 几种常见的模板
    public static final String[] defaultPattern = new String[]{"yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyyMMdd'T'HHmmss",
            "yyyy-MM-dd'T'HH:mm:ss", "yyyy/MM/dd HH:mm:ss", "yyyyMMddHHmm", "yyyyMMdd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyyMMddHH", "yyyyMMdd",
            "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMM", "yyyy-MM", "yyyy/MM"};

    public static String formatUTC(long millis, String pattern) {
        // formatUTC没有办法输入时区
        return DateFormatUtils.format(millis, pattern, DateUtilsEx.LOCAL_UTC_TIME_ZONE);
    }

    public static String formatUTC(long millis) {
        // formatUTC没有办法输入时区
        return DateUtilsEx.formatUTC(millis, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    public static String format(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date parseDate(String str) throws ParseException {
        return DateUtils.parseDate(str, defaultPattern);
    }

    public static Date parseDate(String str, String pattern) throws ParseException {
        return DateUtils.parseDate(str, pattern);
    }

//    public static Date parseDate(Map<String, String> map, String key, String pattern) {
//        Date date = null;
//        try {
//            String strValue = MapUtils.getString(map, key, "");
//            date = DateUtilsEx.parseDate(strValue, pattern);
//        } catch (ParseException e) {
//        }
//        return date;
//    }

//    public static Date parseDate(Map<String, String> map, String key) {
//        Date date = null;
//        try {
//            String strValue = MapUtils.getString(map, key, "");
//            date = DateUtilsEx.parseDate(strValue);
//        } catch (ParseException e) {
//        }
//        return date;
//    }

    /**
     * 计算时间(不含日期)的毫秒数
     *
     * @param time
     * @return
     */
    public static long time2Long(java.sql.Time time) {
        String strTime = time.toString();
        String[] arrTime = StringUtils.split(strTime, ':');
        int hour = NumberUtils.toInt(arrTime[0]);
        int min = NumberUtils.toInt(arrTime[1]);
        int second = NumberUtils.toInt(arrTime[2]);

        return (second + min * 60l + hour * 3600l) * 1000l;
    }
}
