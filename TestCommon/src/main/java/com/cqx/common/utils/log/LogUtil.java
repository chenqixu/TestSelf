package com.cqx.common.utils.log;

import com.cqx.common.utils.file.PropertyUtil;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil {

    private static final String LOG4J_NAME = "log4j.properties";
    private static final String SPLIT_STR = "\\{\\}";
    private static final String CLASS_NAME = LogUtil.class.getName();
    private static LogLEVEL LOG_LEVEL = LogLEVEL.INFO;//0:error 1:warn 2:info 3:debug

    static {
        init();
    }

    private Class<?> cs;

    private LogUtil(Class<?> cs) {
        this.cs = cs;
    }

    private static void init() {
        //可以搜索下classpath下有没有log4j.properties
        Object obj = new Object();
        String path = obj.getClass().getResource("/").getPath() + LOG4J_NAME;
        //判断文件是否存在
        File confFile = new File(path);
        if (confFile.exists() && confFile.isFile()) {
            //读取配置
            PropertyUtil propertyUtil = new PropertyUtil(path);
            //默认是INFO级别
            String level = propertyUtil.getProperty("log4j.rootLogger", LogLEVEL.INFO.getDesc());
            try {
                LOG_LEVEL = Enum.valueOf(LogLEVEL.class, level);
                System.out.println("\033[31;0m" + "====LogUtil====[load conf path]" + path + "，[get log level]" + LOG_LEVEL + "\033[0m");
            } catch (Exception e) {
                System.out.println("\033[31;0m" + "====LogUtil====[load conf path]" + path + " parse error，use defaults log level." + "\033[0m");
                LOG_LEVEL = LogLEVEL.INFO;
            }
        } else {
            System.out.println("\033[31;0m" + "====LogUtil====" + LOG4J_NAME + " not find，use defaults log level." + "\033[0m");
        }
    }

    public static void setLogLevel(LogLEVEL LEVEL) {
        LOG_LEVEL = LEVEL;
    }

    public static LogUtil getLogger(Class<?> cs) {
        LogUtil log = new LogUtil(cs);
        return log;
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    private StackTraceElement getLineInfo() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (!CLASS_NAME.equals(ste[i].getClassName())) {
                return ste[i];
            }
        }
        return null;
    }

    private String getHeader() {
        StringBuffer sb = new StringBuffer();
        long time = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        sb.append(simpleDateFormat.format(new Date(time)))//时间 yyyy-MM-dd HH:mm:ss.SSS
                .append(" [")
                .append(getThreadName())//当前线程名
                .append("] ")
                .append(LOG_LEVEL.getDesc())//日志级别
                .append(" ")
//                .append(cs.getCanonicalName())//类名
                .append(getLineInfo())//获取代码行号、方法名
                .append(" - ");
        return sb.toString();
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
        System.out.println(getHeader() + msg);
    }

    private void println(String msg, Object... param) {
        println(join(msg, param));
    }

    public void warn(String msg) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.WARN.getLevel()) println(msg);
    }

    public void warn(String msg, Object... param) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.WARN.getLevel()) println(msg, param);
    }

    public void info(String msg) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.INFO.getLevel()) println(msg);
    }

    public void info(String msg, Object... param) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.INFO.getLevel()) println(msg, param);
    }

    public void debug(String msg) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.DEBUG.getLevel()) println(msg);
    }

    public void debug(String msg, Object... param) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.DEBUG.getLevel()) println(msg, param);
    }

    public void error(String msg) {
        error(msg, null);
    }

    public void error(String msg, Exception ex) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.ERROR.getLevel()) println(msg);
        if (ex != null) ex.printStackTrace();
    }
}
