package com.cqx.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private static final String SPLIT_STR = "\\{\\}";
    private static LogUtil log = new LogUtil();
    private static LogLEVEL LOG_LEVEL = LogLEVEL.INFO;//0:error 1:warn 2:info 3:debug

    private LogUtil() {
    }

    public static void setLogLevel(LogLEVEL LEVEL) {
        LOG_LEVEL = LEVEL;
    }

    public static LogUtil getInstance() {
        if (log == null) log = new LogUtil();
        return log;
    }

    public String getHeader() {
        long time = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss,SSS");
        return simpleDateFormat.format(new Date(time)) + " [" + LOG_LEVEL.getDesc() + "] - ";
    }

    private String join(String msg, Object... param) {
        StringBuffer sb = new StringBuffer();
        String[] msgArr = msg.split(SPLIT_STR, -1);
        if (msgArr != null && param != null) {
            if (msgArr.length > 0 && param.length > 0 && (msgArr.length - 1 == param.length)) {
                //从第二个开始
                for (int i = 1; i < msgArr.length; i++) {
                    sb.append(param[i - 1] + msgArr[i]);
                }
            } else {
                sb.append(msg);
            }
        } else {
            sb.append(msg);
        }
        return sb.toString();
    }

    private void println(String msg) {
        System.out.println(msg);
    }

    public void warn(String msg) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.WARN.getLevel()) println(getHeader() + msg);
    }

    public void warn(String msg, Object... param) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.WARN.getLevel()) println(getHeader() + join(msg, param));
    }

    public void info(String msg) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.INFO.getLevel()) println(getHeader() + msg);
    }

    public void info(String msg, Object... param) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.INFO.getLevel()) println(getHeader() + join(msg, param));
    }

    public void debug(String msg) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.DEBUG.getLevel()) println(getHeader() + msg);
    }

    public void debug(String msg, Object... param) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.DEBUG.getLevel()) println(getHeader() + join(msg, param));
    }

    public void error(String content) {
        error(content, null);
    }

    public void error(String content, Exception ex) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.ERROR.getLevel()) println(getHeader() + content);
        if (ex != null) ex.printStackTrace();
    }
}
