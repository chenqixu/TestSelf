package com.cqx.common.utils;

import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Date;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);
    /**
     * 支持的格式如下：<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMMddHHmmss"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy-MM-dd HH:mm:ss"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMMdd'T'HHmmss"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy-MM-dd'T'HH:mm:ss"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy-MM-dd'T'HH:mm:ss.SSS"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy/MM/dd HH:mm:ss"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMMddHHmm"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMMdd HH:mm:ss"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy-MM-dd HH:mm"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMMddHH"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMMdd"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy-MM-dd"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy/MM/dd"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyyMM"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy-MM"<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;"yyyy/MM"
     */
    private static final String[] defaultPattern = new String[]{
            "yyyyMMddHHmmss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm:ss.SSS",
            "yyyyMMdd'T'HHmmss", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy/MM/dd HH:mm:ss", "yyyyMMddHHmm",
            "yyyyMMdd HH:mm:ss", "yyyy-MM-dd HH:mm",
            "yyyyMMddHH", "yyyyMMdd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyyMM", "yyyy-MM", "yyyy/MM"
    };

    /**
     * 休眠多少毫秒
     *
     * @param millis 要休眠的毫秒数
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 获取当前时间，格式：yyyyMMddHHmmss
     *
     * @return
     */
    public static String getNow() {
        return getNow("yyyyMMddHHmmss");
    }

    /**
     * 获取当前时间，根据传入的format进行格式化
     *
     * @param format 格式化
     * @return
     */
    public static String getNow(String format) {
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(now);
    }

    /**
     * 把字符串时间格式化成long（时间戳），支持多种格式<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;不在支持的格式内可能会有异常值
     *
     * @param time 时间字符串
     * @return
     * @throws ParseException
     * @see Utils#defaultPattern
     */
    public static long getTime(String time) throws ParseException {
        return DateUtils.parseDate(time, defaultPattern).getTime();
    }

    /**
     * 仅适用于JDK8以上的版本，计算微秒，在秒的基础上*1000*1000
     *
     * @param time
     * @return
     */
    public static long formatTimeByJDK8(String time) {
        LocalDateTime ldt = LocalDateTime.parse(time);
        long second = ldt.toEpochSecond(ZoneOffset.of("+8"));
        long microSecond = ldt.toInstant(ZoneOffset.of("+8")).getLong(ChronoField.MICRO_OF_SECOND);
        return (second * 1000 * 1000 + microSecond);
    }

    /**
     * 把字符串时间根据传入的foramt格式化成long（时间戳）
     *
     * @param time   时间字符串
     * @param format 格式化
     * @return
     * @throws ParseException
     */
    public static long formatTime(String time, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(time).getTime();
    }

    /**
     * 把字符串时间根据yyyy-MM-dd HH:mm:ss格式化成long（时间戳）
     *
     * @param time 时间字符串
     * @return
     * @throws ParseException
     */
    public static long formatTime(String time) throws ParseException {
        return formatTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 把传入的时间戳根据传入的foramt格式化成字符串时间
     *
     * @param time   时间戳
     * @param format 格式化
     * @return
     */
    public static String formatTime(long time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(time));
    }

    /**
     * 把传入的时间戳根据yyyy-MM-dd HH:mm:ss格式化成字符串时间
     *
     * @param time 时间戳
     * @return
     */
    public static String formatTime(long time) {
        return formatTime(time, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 获取系统属性
     */
    public static String getSystemProperty(String args) {
        return System.getProperty(args);
    }

    /**
     * 获取字符集，默认返回GB2312
     */
    public static String getFileEncoding() {
        String fileencoding = Utils.getSystemProperty("file.encoding");
        return fileencoding == null ? "GB2312" : fileencoding;
    }

    /**
     * 是否是本地
     *
     * @return
     */
    public static boolean isWindow() {
        String systemType = System.getProperty("os.name");
        if (systemType.toUpperCase().startsWith("WINDOWS")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * long转byte数组
     *
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * byte数组转long
     *
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.put(bytes, 0, bytes.length);
        buffer.flip();//need flip
        return buffer.getLong();
    }
}
