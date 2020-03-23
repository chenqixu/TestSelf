package com.cqx.common.utils.log;

import com.cqx.common.utils.file.PropertyUtil;

import java.io.*;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyLoggerFactory implements MyLogger {

    private static final String LOG4J_NAME = "log4j.properties";
    private static final String LOG4J_ROOTLOGGER = "log4j.rootLogger";
    private static final String SPLIT_STR = "\\{\\}";
    private static final String CLASS_NAME = MyLoggerFactory.class.getName();
    private static final String SIMPLE_CLASS_NAME = MyLoggerFactory.class.getSimpleName();
    private static LogLEVEL LOG_LEVEL = LogLEVEL.INFO;//0:error 1:warn 2:info 3:debug
    private static PrintStream printStream;//输出流
    private static volatile boolean isLoad = false;//是否需要重新加载

    static {
        init(null);
    }

    private Class<?> cs;

    private MyLoggerFactory(Class<?> cs) {
        this.cs = cs;
    }

    /**
     * 初始化
     *
     * @param cs
     */
    private static void init(Class<?> cs) {
        //设置输出流
        setPrintStream(System.out);
        //可以搜索下classpath下有没有log4j.properties
        String path = getClassPath(cs);
        //判断文件是否存在
        File confFile = new File(path);
        //三种情况：1、path没有；2、log4j.properties没有；3、有log4j.properties
        if (path.equals("")) {
            printStream.println(getRedString(SIMPLE_CLASS_NAME + "：Static output is used, waiting for the second load of getLogger method."));
        } else if (confFile.exists() && confFile.isFile()) {
            //读取配置
            PropertyUtil propertyUtil = new PropertyUtil(path);
            //默认是INFO级别
            String level = propertyUtil.getProperty(LOG4J_ROOTLOGGER, LogLEVEL.INFO.getDesc());
            try {
                LOG_LEVEL = Enum.valueOf(LogLEVEL.class, level);
                printStream.println(getRedString(SIMPLE_CLASS_NAME + "：Load conf path in [" + path + "]，Get log level [" + LOG_LEVEL + "]"));
            } catch (Exception e) {
                printStream.println(getRedString(SIMPLE_CLASS_NAME + "：Load conf path in [" + path + "] Parse error，use defaults log level."));
                LOG_LEVEL = LogLEVEL.INFO;
            }
        } else {
            printStream.println(getRedString(SIMPLE_CLASS_NAME + "：" + LOG4J_NAME + " not find，use defaults log level."));
        }
    }

    /**
     * 设置日志级别
     *
     * @param LEVEL
     */
    public static void setLogLevel(LogLEVEL LEVEL) {
        LOG_LEVEL = LEVEL;
    }

    /**
     * 获取一个日志实例
     *
     * @param cs
     * @return
     */
    public static MyLoggerFactory getLogger(Class<?> cs) {
        if (isLoad) init(cs);//如果需要重新加载，则进行重新加载
        MyLoggerFactory log = new MyLoggerFactory(cs);
        return log;
    }

    /**
     * 获取类路径
     *
     * @param cs
     * @return
     */
    private static String getClassPath(Class<?> cs) {
        String path = "";
        if (cs == null) {
            Object obj = new Object();
            URL classResource = obj.getClass().getResource("/");
            if (classResource != null) {
                path = classResource.getPath() + LOG4J_NAME;
                isLoad = false;//加载到了，不需要重复加载
            } else {
                isLoad = true;//需要重新加载配置，由于类加载顺序问题，导致这里获取不到Resource路径
            }
        } else {
            URL classResource = cs.getResource("/");
            if (classResource != null) {
                path = classResource.getPath() + LOG4J_NAME;
                isLoad = false;//加载到了，不需要重复加载
            }
        }
        return path;
    }

    /**
     * 字符转换成红色
     *
     * @param msg
     * @return
     */
    private static String getRedString(String msg) {
        return "\033[31;0m" + msg + "\033[0m";
    }

    /**
     * 设置输出流
     *
     * @param printStream
     */
    public static void setPrintStream(PrintStream printStream) {
        MyLoggerFactory.printStream = printStream;
    }

    /**
     * 获取当前线程名
     *
     * @return
     */
    private String getThreadName() {
        return Thread.currentThread().getName();
    }

    /**
     * 获取行号
     *
     * @return
     */
    private StackTraceElement getLineInfo() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            if (!CLASS_NAME.equals(ste[i].getClassName())) {
                return ste[i];
            }
        }
        return null;
    }

    /**
     * 拼接日志头部内容
     *
     * @return
     */
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
        printStream.println(getHeader() + msg);
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

    public void error(String msg, Throwable throwable) {
        if (LOG_LEVEL.getLevel() >= LogLEVEL.ERROR.getLevel()) println(msg);
        if (throwable != null) throwable.printStackTrace();
    }

    /**
     * 获取堆栈并输出
     *
     * @param e
     */
    private void getStackTrace(Throwable e) {
        // 使用Stream
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        PrintStream printStream = new PrintStream(byteArrayOutputStream);
//        e.printStackTrace(printStream);
//        byte b[] = byteArrayOutputStream.toByteArray();
//        String result = new String(b, StandardCharsets.UTF_8);
//        printStream.println(result);

        // 使用Writer
        // 输出流（可以是String，也可以是CharArray）
        StringWriter writer = new StringWriter();
//        CharArrayWriter writer = new CharArrayWriter();
        // 打印输出流
        PrintWriter printWriter = new PrintWriter(writer);
        // 错误堆栈往打印输出流进行输出
        e.printStackTrace(printWriter);
        // 读取流从输出流进行读取
        BufferedReader bufferedReader = new BufferedReader(new StringReader(writer.toString()));
        // 按行读取
        try {
            String msg;
            while ((msg = bufferedReader.readLine()) != null) {
                printStream.println(msg);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            printWriter.close();
        }
    }
}
